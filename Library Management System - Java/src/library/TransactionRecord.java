package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TransactionRecord {

    private int id;
    private BookItem book;
    private UserAccount user;
    private LocalDate issuedOn;
    private LocalDate deadline;
    private LocalDate returnedOn;

    public TransactionRecord(int id, BookItem book, UserAccount user, LocalDate issuedOn, LocalDate deadline) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.issuedOn = issuedOn;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public boolean isClosed() {
        return returnedOn != null;
    }

    public void markReturned(LocalDate date) {
        this.returnedOn = date;
    }

    public long getPenalty(int perDayFine) {
        if (returnedOn == null || !returnedOn.isAfter(deadline)) return 0;
        long days = ChronoUnit.DAYS.between(deadline, returnedOn);
        return days * perDayFine;
    }

    @Override
    public String toString() {
        return "Txn#" + id +
                " | Book: " + book.getName() +
                " | User: " + user.getUsername() +
                " | Issued: " + issuedOn +
                " | Due: " + deadline +
                " | Returned: " + (returnedOn == null ? "Pending" : returnedOn);
    }
}