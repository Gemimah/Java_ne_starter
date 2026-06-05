-- Task 6: Database routines and messaging (PostgreSQL)
-- Run after tables are created:
-- psql -U postgres -h localhost -d exam_db -f scripts/utility-db-routines.sql

-- Ensure role check supports all required roles.
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE IF EXISTS users
    ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'OPERATOR', 'FINANCE', 'CUSTOMER'));

CREATE OR REPLACE FUNCTION fn_insert_bill_message()
RETURNS trigger AS
$$
DECLARE
    customer_name TEXT;
BEGIN
    SELECT full_names INTO customer_name FROM customers WHERE id = NEW.customer_id;
    INSERT INTO messages(customer_id, month_year, amount, text, created_at)
    VALUES (
        NEW.customer_id,
        NEW.billing_month || '/' || NEW.billing_year,
        NEW.total_amount,
        'Dear ' || COALESCE(customer_name, 'Customer') || ', Your '
            || NEW.billing_month || '/' || NEW.billing_year
            || ' utility bill of ' || NEW.total_amount
            || ' FRW has been successfully processed.',
        now()
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_bill_message ON bills;
CREATE TRIGGER trg_bill_message
AFTER INSERT ON bills
FOR EACH ROW
EXECUTE FUNCTION fn_insert_bill_message();

CREATE OR REPLACE FUNCTION fn_set_paid_status_and_message()
RETURNS trigger AS
$$
DECLARE
    bill_month INT;
    bill_year INT;
    customer_name TEXT;
BEGIN
    IF NEW.outstanding_balance <= 0 THEN
        NEW.outstanding_balance := 0;
        NEW.status := 'PAID';
    END IF;

    IF NEW.status = 'PAID' AND OLD.status <> 'PAID' THEN
        SELECT full_names INTO customer_name FROM customers WHERE id = NEW.customer_id;
        bill_month := NEW.billing_month;
        bill_year := NEW.billing_year;
        INSERT INTO messages(customer_id, month_year, amount, text, created_at)
        VALUES (
            NEW.customer_id,
            bill_month || '/' || bill_year,
            NEW.total_amount,
            'Dear ' || COALESCE(customer_name, 'Customer') || ', Your '
                || bill_month || '/' || bill_year
                || ' utility bill of ' || NEW.total_amount
                || ' FRW has been successfully processed.',
            now()
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_bill_paid_message ON bills;
CREATE TRIGGER trg_bill_paid_message
BEFORE UPDATE ON bills
FOR EACH ROW
EXECUTE FUNCTION fn_set_paid_status_and_message();
