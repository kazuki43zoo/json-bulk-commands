package com.example.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddingFieldProcessor {

  static final AddingFieldProcessor INSTANCE = new AddingFieldProcessor();

  private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
      .create();

  private AddingFieldProcessor() {
    // NOP
  }

  void execute(List<String> fieldNames, List<String> fieldValues, Path file, Map<String, Object> valueMappings) {
    try {
      DocumentContext documentContext = JsonPath.parse(file.toFile());
      StandardEvaluationContext context = new StandardEvaluationContext(documentContext.json());
      context.setVariable("_valueMappings", valueMappings);
      context.setVariable("_documentContext", documentContext);
      Map<String, Object> items = new LinkedHashMap<>();
      for (int i = 0; i < fieldNames.size(); i++) {
        Expression expression = EXPRESSION_PARSER.parseExpression(fieldValues.get(i));
        items.put(fieldNames.get(i), expression.getValue(context));
      }
      for (Map.Entry<String, Object> item : items.entrySet()) {
        String path = item.getKey().substring(0, item.getKey().lastIndexOf("."));
        String key = item.getKey().substring(item.getKey().lastIndexOf(".") + 1);
        documentContext.put(path, key, item.getValue());
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
