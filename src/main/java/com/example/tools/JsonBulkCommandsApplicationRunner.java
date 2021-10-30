package com.example.tools;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JsonBulkCommandsApplicationRunner implements ApplicationRunner, ExitCodeGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonBulkCommandsApplicationRunner.class);

  static {
    Configuration.defaultConfiguration().jsonProvider(new JacksonJsonProvider());
  }

  private int exitCode;

  @Override
  public void run(ApplicationArguments args) throws IOException {
    if (args.getSourceArgs().length == 0 || args.containsOption("h") || args.containsOption("help")) {
      System.out.println();
      System.out.println("[Command arguments]");
      System.out.println("  --command");
      System.out.println("       adding-fields deleting-fields updating-fields formatting");
      System.out.println("  --dir");
      System.out.println("       target directory for apply command(can search target files on specified directory)");
      System.out.println("  --files");
      System.out.println("       target files for apply command(can filter that ending with specified file name)");
      System.out.println("  --field-names");
      System.out.println("       list of field name using JsonPath(e.g: $.field2 )");
      System.out.println("  --field-values");
      System.out.println(
          "       list of field value(can reference other field values using SpEL expression and JsonPath)");
      System.out.println("  --value-mapping-files");
      System.out.println("       mapping yaml files for value converting");
      System.out.println(
          "       can be accessed using an SpEL like as #_valueMappings[{value-name}][{value}] (e.g. --field-names=$.foo --field-values=#_valueMappings[foo][#root[foo]]?:'0')");
      System.out.println("       e.g.) value mapping yaml file");
      System.out.println("       foo:");
      System.out.println("         \"10\": \"1\"");
      System.out.println("         \"20\": \"2\"");
      System.out.println("       bar:");
      System.out.println("         \"10\": \"2\"");
      System.out.println("         \"20\": \"1\"");
      System.out.println("  --h (--help)");
      System.out.println("       print help");
      System.out.println();
      System.out.println("[Exit Code]");
      System.out.println("  0 : There is no difference (normal end)");
      System.out.println("  1 : Was occurred an application error");
      System.out.println("  2 : Command arguments invalid");
      System.out.println();
      System.out.println("[Usage: adding-fields]");
      System.out.println("  Adding specified new field using field-names and field-values.");
      System.out.println(
          "  e.g.) --command=adding-fields --dir=src/test/resources/data --files=xxx.json,yyy.json --column-names=$.field2,$.field3 --column-values=1,#root[field1]");
      System.out.println("  ------------------------");
      System.out.println("  {\"field1\":\"12345\"}");
      System.out.println("  ------------------------");
      System.out.println("    ↓");
      System.out.println("  ------------------------");
      System.out.println("  {\"field1\":\"12345\", \"field2\":1, \"field3\":\"12345\"}");
      System.out.println("  ------------------------");
      System.out.println();
      System.out.println("[Usage: deleting-fields]");
      System.out.println("  Deleting specified existing field using field-names.");
      System.out.println(
          "  e.g.) --command=deleting-fields --dir=src/test/resources/data --files=xxx.json,yyy.json --field-names=$.field2");
      System.out.println("  ------------------------");
      System.out.println("  {\"field1\":\"12345\", \"field2\":1, \"field3\":\"12345\"}");
      System.out.println("  ------------------------");
      System.out.println("    ↓");
      System.out.println("  ------------------------");
      System.out.println("  {\"field1\":\"12345\", \"field3\":\"12345\"}");
      System.out.println("  ------------------------");
      System.out.println();
      System.out.println("[Usage: updating-fields]");
      System.out.println("  Updating value specified existing field using fields-names and fields-values.");
      System.out.println(
          "  e.g.) --command=updating-fields --dir=src/test/resources/data --files=xxx.json,yyy.json --field-names=$.[*].field2 --field-values='0'");
      System.out.println("  ------------------------");
      System.out.println("  [{\"field1\":\"12345\", \"field2\":\"1\"}, {\"field1\":\"67890\", \"field2\":\"2\"}]");
      System.out.println("  ------------------------");
      System.out.println("    ↓");
      System.out.println("  ------------------------");
      System.out.println("  [{\"field1\":\"12345\", \"field2\":\"0\"}, {\"field1\":\"67890\", \"field2\":\"0\"}]");
      System.out.println("  ------------------------");
      System.out.println();
      System.out.println("[Usage: formatting]");
      System.out.println("  Formatting json to pretty format.");
      System.out.println(
          "  e.g.) --command=formatting --dir=src/test/resources/data --files=xxx.json,yyy.json");
      System.out.println();
      return;
    }

    String command;
    if (args.containsOption("command")) {
      command = args.getOptionValues("command").stream().findFirst().orElse("");
    } else {
      this.exitCode = 2;
      LOGGER.warn(
          "'command' is required. valid-commands:[adding-fields, deleting-fields, updating-fields]");
      return;
    }

    String dir;
    if (args.containsOption("dir")) {
      dir = args.getOptionValues("dir").stream().findFirst()
          .orElseThrow(() -> new IllegalArgumentException("'dir' value is required."));
    } else {
      this.exitCode = 2;
      LOGGER.warn("'dir' is required.");
      return;
    }

    List<String> files;
    if (args.containsOption("files")) {
      files = args.getOptionValues("files").stream().flatMap(x -> StringUtils.commaDelimitedListToSet(x).stream())
          .distinct().collect(
              Collectors.toList());
    } else {
      this.exitCode = 2;
      LOGGER.warn("'files' is required.");
      return;
    }

    List<String> fieldNames = args.containsOption("field-names") ?
        args.getOptionValues("field-names").stream()
            .flatMap(x -> Stream.of(StringUtils.commaDelimitedListToStringArray(x))).collect(Collectors.toList()) :
        Collections.emptyList();

    List<String> fieldValues = args.containsOption("field-values") ?
        args.getOptionValues("field-values").stream()
            .flatMap(x -> Stream.of(StringUtils.commaDelimitedListToStringArray(x))).collect(Collectors.toList()) :
        Collections.emptyList();

    final Map<String, Object> valueMappings;
    if (args.containsOption("value-mapping-files")) {
      YamlMapFactoryBean yamlMapFactoryBean = new YamlMapFactoryBean();
      yamlMapFactoryBean.setResources(
          args.getOptionValues("value-mapping-files").stream().map(FileSystemResource::new).toArray(
              Resource[]::new));
      valueMappings = yamlMapFactoryBean.getObject();
    } else {
      valueMappings = Collections.emptyMap();
    }

    LOGGER.info("Start. command:{} dir:{} files:{} field-names:{} field-values:{} value-mappings:{}",
        command, dir, files, fieldNames, fieldValues, valueMappings);

    try {
      Files.walk(Paths.get(dir))
          .filter(Files::isRegularFile)
          .filter(file -> files.stream().anyMatch(x -> file.toString().replace('\\', '/').endsWith(x)))
          .sorted().forEach(file -> execute(command, fieldNames, fieldValues, file, valueMappings));
    }
    catch (IllegalArgumentException e) {
      this.exitCode = 2;
      LOGGER.warn(e.getMessage());
    }

    LOGGER.info("End.");

  }

  private void execute(String command, List<String> fieldNames, List<String> fieldValues, Path file,
      Map<String, Object> valueMappings) {
    LOGGER.info("processing file:{}", file);
    switch (command) {
    case "adding-fields":
      if (fieldNames.isEmpty()) {
        throw new IllegalArgumentException("'field-names' is required.");
      }
      if (fieldNames.size() != fieldValues.size()) {
        throw new IllegalArgumentException("'field-names' and 'field-values' should be same size.");
      }
      AddingFieldProcessor.INSTANCE.execute(fieldNames, fieldValues, file, valueMappings);
      break;
    case "deleting-fields":
      if (fieldNames.isEmpty()) {
        throw new IllegalArgumentException("'field-names' is required.");
      }
      DeletingFieldProcessor.INSTANCE.execute(fieldNames, file);
      break;
    case "updating-fields":
      if (fieldNames.isEmpty()) {
        throw new IllegalArgumentException("'field-names' is required.");
      }
      if (fieldNames.size() != fieldValues.size()) {
        throw new IllegalArgumentException("'field-names' and 'field-values' should be same size.");
      }
      UpdatingFieldProcessor.INSTANCE.execute(fieldNames, fieldValues, file, valueMappings);
      break;
    case "formatting":
      FormattingProcessor.INSTANCE.execute(file);
      break;
    default:
      throw new IllegalArgumentException(String.format("'%s' command not support. valid-commands:%s", command,
          "[adding-fields, deleting-fields, updating-fields]"));
    }

  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

}
