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
import java.util.List;

public class DeletingFieldProcessor {

  static final DeletingFieldProcessor INSTANCE = new DeletingFieldProcessor();

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private DeletingFieldProcessor() {
    // NOP
  }

  void execute(List<String> fieldNames, Path file) {
    try {
      DocumentContext documentContext = JsonPath.parse(file.toFile());
      for (String itemName : fieldNames) {
        documentContext.delete(itemName);
      }
      try (Writer writer = Files.newBufferedWriter(file)) {
        GSON.toJson((Object) documentContext.json(), writer);
      }
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
