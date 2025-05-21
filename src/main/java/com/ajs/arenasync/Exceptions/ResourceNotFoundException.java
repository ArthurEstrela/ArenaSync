package com.ajs.arenasync.Exceptions;

public class ResourceNotFoundException extends ArenaSyncException {
  public ResourceNotFoundException(String resource, Long id) {
    super(resource + " with ID " + id + " not found.");
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
