//JobNotFoundException
package com.openfuture.Exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {
        super("Job not found with id: " + id);
    }
}
