package ru.practicum.ewm.model.event;

public enum EventStateAction {
    PUBLISH_EVENT,
    REJECT_EVENT,
    SEND_TO_REVIEW,
    CANCEL_REVIEW
}