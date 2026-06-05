-- ============================================================
-- Task 6: Database-level routines (PostgreSQL)
-- Run AFTER the app has started once (so all tables exist):
--   set PGPASSWORD=symbols9
--   "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d exam_db -f scripts\utility-db-routines.sql
--
-- The Spring application already creates in-app notifications + emails.
-- This script adds an INDEPENDENT database trigger + function that writes the
-- required message into an audit table (notification_log) whenever a bill is
-- APPROVED or fully PAID. This demonstrates the DBMS routine requirement
-- without duplicating the application's own notifications table.
-- ============================================================

-- 0) Make sure the users role check allows all four roles
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE IF EXISTS users
    ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'OPERATOR', 'FINANCE', 'CUSTOMER'));

-- 1) Clean up older demo objects if they exist
DROP TRIGGER IF EXISTS trg_bill_message ON bills;
DROP TRIGGER IF EXISTS trg_bill_paid_message ON bills;
DROP FUNCTION IF EXISTS fn_insert_bill_message();
DROP FUNCTION IF EXISTS fn_set_paid_status_and_message();
DROP TABLE IF EXISTS messages;

-- 2) Audit table written ONLY by the database routine
CREATE TABLE IF NOT EXISTS notification_log (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT,
    month_year  VARCHAR(20),
    amount      NUMERIC(12,2),
    message     TEXT,
    created_at  TIMESTAMP DEFAULT now()
);

-- 3) Function: build the message in the required format
CREATE OR REPLACE FUNCTION fn_log_bill_event()
RETURNS trigger AS
$$
DECLARE
    customer_name TEXT;
    my            TEXT;
BEGIN
    -- Fire only when a bill becomes APPROVED or PAID
    IF (NEW.status = 'APPROVED' AND OLD.status IS DISTINCT FROM 'APPROVED')
       OR (NEW.status = 'PAID' AND OLD.status IS DISTINCT FROM 'PAID') THEN

        SELECT full_names INTO customer_name FROM customers WHERE id = NEW.customer_id;
        my := NEW.billing_month || '/' || NEW.billing_year;

        INSERT INTO notification_log(customer_id, month_year, amount, message, created_at)
        VALUES (
            NEW.customer_id,
            my,
            NEW.total_amount,
            'Dear ' || COALESCE(customer_name, 'Customer') || ', Your ' || my
                || ' utility bill of ' || NEW.total_amount
                || ' FRW has been successfully processed.',
            now()
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4) Trigger: after a bill row is updated (approval / payment status change)
DROP TRIGGER IF EXISTS trg_log_bill_event ON bills;
CREATE TRIGGER trg_log_bill_event
AFTER UPDATE ON bills
FOR EACH ROW
EXECUTE FUNCTION fn_log_bill_event();

-- View results with:  SELECT * FROM notification_log ORDER BY id;
