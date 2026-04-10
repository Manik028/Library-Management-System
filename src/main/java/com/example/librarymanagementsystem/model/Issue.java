package com.example.librarymanagementsystem.model;

import java.time.LocalDate;

public class Issue {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;

    public Issue() {}

    public Issue(int id, int bookId, int memberId, LocalDate issueDate, LocalDate returnDate, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}