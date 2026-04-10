package com.example.librarymanagementsystem.model;

public class Member {
    private int id;
    private String name;
    private String contact;

    // New fields for the table
    private String issuedBook;
    private String issueDate;
    private String returnDate;

    public Member(int id, String name, String contact, String issuedBook, String issueDate, String returnDate) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.issuedBook = issuedBook;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    // Old constructor in case you need it elsewhere
    public Member(int id, String name, String contact) {
        this(id, name, contact, null, null, null);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getIssuedBook() { return issuedBook; }
    public String getIssueDate() { return issueDate; }
    public String getReturnDate() { return returnDate; }

    // THIS MAKES THE MEMBER DROPDOWN SHOW THE NAME
    @Override
    public String toString() {
        return name;
    }
}