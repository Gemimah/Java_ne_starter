package com.gemimah.nestartertemplate.entity;

// Lifecycle of a utility bill.
public enum BillStatus {
	PENDING,        // just generated, awaiting finance/admin approval
	APPROVED,       // approved, customer notified, awaiting payment
	PARTIALLY_PAID, // some money paid but balance remains
	PAID,           // fully settled
	OVERDUE         // approved/unpaid and due date passed (penalty applied)
}
