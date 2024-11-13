package com.openfuture.Exception;

public class InvalidJobDataException extends Throwable {
    public InvalidJobDataException(String jobTitleCannotBeEmpty) {
        super(jobTitleCannotBeEmpty);
    }
}
