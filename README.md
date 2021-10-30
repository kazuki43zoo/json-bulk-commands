# json-bulk-commands

Bulk operation utilities for json file.

## Features

Support following features.

* Adding fields by specified expression(fixed value or dynamic value) at the end
* Deleting fields
* Updating fields by specified expression(fixed value or dynamic value)

## Related libraries document

* [JsonPath](https://github.com/json-path/JsonPath)
* [SpEL provided by Spring Framework](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)

## How to specify target files

Search files that matches conditions specified by `--dir` and `--files`.

* You need to specify a base directory using the `--dir`
* You need to specify a target file path suffix using the `--files`

## How to run

### Using Spring Boot Maven Plugin

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=""
```

```
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------< com.example.tools:json-bulk-commands >----------------
[INFO] Building json-bulk-commands 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] >>> spring-boot-maven-plugin:2.5.6:run (default-cli) > test-compile @ json-bulk-commands >>>
[INFO] 
[INFO] --- maven-resources-plugin:3.2.0:resources (default-resources) @ json-bulk-commands ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] Copying 1 resource
[INFO] Copying 0 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ json-bulk-commands ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:3.2.0:testResources (default-testResources) @ json-bulk-commands ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] Copying 2 resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:testCompile (default-testCompile) @ json-bulk-commands ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] <<< spring-boot-maven-plugin:2.5.6:run (default-cli) < test-compile @ json-bulk-commands <<<
[INFO] 
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.5.6:run (default-cli) @ json-bulk-commands ---
[INFO] Attaching agents: []

[Command arguments]
  --command
       adding-fields deleting-fields updating-fields
  --dir
       target directory for apply command(can search target files on specified directory)
  --files
       target files for apply command(can filter that ending with specified file name)
  --field-names
       list of field name using JsonPath(e.g: $.field2 )
  --field-values
       list of field value(can reference other field values using SpEL expression and JsonPath)
  --value-mapping-files
       mapping yaml files for value converting
       can be accessed using an SpEL like as #_valueMappings[{value-name}][{value}] (e.g. --field-names=$.foo --field-values=#_valueMappings[foo][#root[foo]]?:'0')
       e.g.) value mapping yaml file
       foo:
         "10": "1"
         "20": "2"
       bar:
         "10": "2"
         "20": "1"
  --h (--help)
       print help

[Exit Code]
  0 : There is no difference (normal end)
  1 : Was occurred an application error
  2 : Command arguments invalid

[Usage: adding-fields]
  Adding specified new field using field-names and field-values.
  e.g.) --command=adding-fields --dir=src/test/resources/data --files=xxx.json,yyy.json --column-names=$.field2,$.field3 --column-values=1,#root[field1]
  ------------------------
  {"field1":"12345"}
  ------------------------
    ↓
  ------------------------
  {"field1":"12345", "field2":1, "field3":"12345"}
  ------------------------

[Usage: deleting-fields]
  Deleting specified existing field using field-names.
  e.g.) --command=deleting-fields --dir=src/test/resources/data --files=xxx.json,yyy.json --field-names=$.field2
  ------------------------
  {"field1":"12345", "field2":1, "field3":"12345"}
  ------------------------
    ↓
  ------------------------
  {"field1":"12345", "field3":"12345"}
  ------------------------

[Usage: updating-fields]
  Updating value specified existing field using fields-names and fields-values.
  e.g.) --command=updating-fields --dir=src/test/resources/data --files=xxx.json,yyy.json --field-names=$.[*].field2 --field-values='0'
  ------------------------
  [{"field1":"12345", "field2":"1"}, {"field1":"67890", "field2":"2"}]
  ------------------------
    ↓
  ------------------------
  [{"field1":"12345", "field2":"0"}, {"field1":"67890", "field2":"0"}]
  ------------------------

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.603 s
[INFO] Finished at: 2021-10-30T14:36:01+09:00
[INFO] ------------------------------------------------------------------------
```

### Using standalone Java Application

```bash
$ ./mvnw clean verify -DskipTests
```

```
$ java -jar target/json-bulk-commands.jar
```