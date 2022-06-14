package com.dimipet.jcesd;

public class Question {
    private String question;
    private int questionNumber;
    private int[] valueMatrix;
    private int valueSelected;
    private boolean answered;

    public Question() {
    }

    public Question(int questionNumber, String question, 
            int[] valueMatrix, 
            int valueSelected, 
            boolean answered) {
        this.questionNumber=questionNumber;
        this.question = question;
        this.valueMatrix = valueMatrix;
        this.valueSelected = valueSelected;
        this.answered = answered;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int[] getValueMatrix() {
        return valueMatrix;
    }

    public void setValueMatrix(int[] valueMatrix) {
        this.valueMatrix = valueMatrix;
    }

    public int getValueSelected() {
        return valueSelected;
    }

    public void setValueSelected(int valueSelected) {
        this.valueSelected = valueSelected;
    }
}