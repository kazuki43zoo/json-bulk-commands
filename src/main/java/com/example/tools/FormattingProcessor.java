package com.example.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FormattingProcessor {

  static final FormattingProcessor INSTANCE = new FormattingProcessor();

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
      .create();

  private FormattingProcessor() {
    // NOP
  }

  void execute(Path file) {
    try {
      DocumentContext documentContext = JsonPath.parse(file.toFile());
      try (Writer writer = Files.newBufferedWriter(file)) {
        GSON.toJson((Object) documentContext.json(), writer);
      }
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
