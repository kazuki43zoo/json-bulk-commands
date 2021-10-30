package com.example.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import java.io.IOException;

class JsonBulkCommandsApplicationRunnerTests {

  private final JsonBulkCommandsApplicationRunner runner = new JsonBulkCommandsApplicationRunner();

  @Test
  void addingFields() throws IOException {
    String[] args = { "--command=adding-fields", "--files=a.json", "--field-names=$.[*].field2",
        "--field-values='0'",
        "--dir=target/test-classes/data" };
    runner.run(new DefaultApplicationArguments(args));
  }

  @Test
  void updatingFields() throws IOException {
    String[] args = { "--command=updating-fields", "--files=a.json", "--field-names=$.[*].field1",
        "--field-values='0'",
        "--dir=target/test-classes/data" };
    runner.run(new DefaultApplicationArguments(args));
  }

  @Test
  void deletingFields() throws IOException {
    String[] args = { "--command=deleting-fields", "--files=a.json", "--field-names=$.[*].field3",
        "--dir=target/test-classes/data" };
    runner.run(new DefaultApplicationArguments(args));
  }

  @Test
  void noArgs() throws IOException {
    String[] args = {};
    runner.run(new DefaultApplicationArguments(args));
  }

  @Test
  void h() throws IOException {
    String[] args = { "--h" };
    runner.run(new DefaultApplicationArguments(args));
  }

  @Test
  void help() throws IOException {
    String[] args = { "--help" };
    runner.run(new DefaultApplicationArguments(args));
  }
}
