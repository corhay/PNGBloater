# PNGBloater
CLI tool to seamlessly bloat png files without affecting quality

## Concept

> Multiply or reduce the file size of a PNG file without affecting the image itself.

`pngbloater` is a small command-line tool written in Java with [picocli](https://picocli.info/). It provides two subcommands: `bloat` to artificially increase the size of a PNG file, and `debloat` to bring a bloated PNG back to a smaller size. The pixels of the image are never changed.

## Table of contents

- [Authors](#authors)
- [Purpose](#purpose)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Error handling](#error-handling)
- [Project structure](#project-structure)

## Authors

| Name | Contact |
|------|---------|
| Manuel Cabras | manuel.cabras@heig-vd.ch |
| Joao Da Luz | joao.daluzentradas@heig-vd.ch |
| Corwin Hayslip | corwin.hayslip@heig-vd.ch |

## Purpose

PNG files can carry extra data without changing how the image looks. `pngbloater` uses this to:

- **bloat** a PNG: multiply its file size by a chosen factor, which is useful for testing upload limits, storage quotas or network transfer with realistic files;
- **debloat** a PNG: remove the extra data and restore a compact file.

In both cases the displayed image stays identical.

## Requirements

- [Git](https://git-scm.com/)
- JDK 21 or newer

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/corhay/PNGBloater.git
cd PNGBloater/PNGBloater
```

### 2. Build with Maven

```bash
./mvnw clean package
```

The build compiles the sources and uses the Maven Shade plugin to package the application and its dependencies (picocli, Apache Commons IO) into a single executable JAR in the `target/` directory.

### 3. Run the application

```bash
java -jar target/PNGBloater-1.0.jar --help
```

> **Note:** in the examples below, `pngbloater` stands for `java -jar target/PNGBloater-1.0.jar`. You can define a shell alias to shorten it:
>
> ```bash
> alias pngbloater='java -jar target/PNGBloater-1.0.jar'
> ```

## Usage

```
pngbloater [-hV] [COMMAND]
```

| Command | Description |
|---------|-------------|
| `bloat` | Multiplies the file size of a PNG file. |
| `debloat` | Reduces a bloated PNG file. |

Global options:

| Option | Description |
|--------|-------------|
| `-h`, `--help` | Show the help message and exit. |
| `-V`, `--version` | Print version information and exit. |

### `bloat`

```
pngbloater bloat [-hV] [-f=<factor>] <file> <output>
```

| Argument / option | Description | Default |
|-------------------|-------------|---------|
| `<file>` | The PNG file to bloat. | required |
| `<output>` | The output file path. | required |
| `-f`, `--factor=<factor>` | Factor by which to multiply the file size. Must be at least 1. | `2` |

### `debloat`

```
pngbloater debloat [-hV] <file> <output>
```

| Argument / option | Description |
|-------------------|-------------|
| `<file>` | The PNG file to debloat. |
| `<output>` | The output file path. |

## Examples

### Show the general help

```console
$ pngbloater --help
Usage: pngbloater [-hV] [COMMAND]
Multiplies or reduces the file size of a PNG file without affecting the image
itself.
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  bloat    Multiplies the file size of a PNG file without affecting the image
             itself.
  debloat  Reduces a bloated PNG file without affecting the image itself.
```

### Bloat a file with the default factor (x2)

```console
$ pngbloater bloat ./assets/example.png ./bloated.png
File ./assets/example.png bloated successfully!
Output file: ./bloated.png
Factor: 2
Mode: bloat
```

### Bloat a file with a custom factor

```console
$ pngbloater bloat ./assets/example.png ./bloated.png --factor 10
File ./assets/example.png bloated successfully!
Output file: ./bloated.png
Factor: 10
Mode: bloat
```

### Debloat a file

```console
$ pngbloater debloat ./assets/example-bloated.png ./unbloated.png
File path: ./assets/example-bloated.png debloated successfully!
Output file: ./unbloated.png
Mode: debloat
```

## Error handling

Invalid input is reported with a clear message followed by the usage of the relevant subcommand. The process exits with code `2` in this case.

**File does not exist**

```console
$ pngbloater bloat missing.png out.png
Given file: 'missing.png' does not exist.
Usage: pngbloater bloat [-hV] [-f=<factor>] <file> <output>
...
```

**File is not a PNG**

```console
$ pngbloater debloat notes.txt out.png
Given file: 'notes.txt' must be a png image.
Usage: pngbloater debloat [-hV] <file> <output>
...
```

**Invalid factor**

```console
$ pngbloater bloat photo.png out.png -f 0
Factor must be larger than 0, given: 0
Usage: pngbloater bloat [-hV] [-f=<factor>] <file> <output>
...
```

**Non-numeric factor** (handled by picocli)

```console
$ pngbloater bloat photo.png out.png -f abc
Invalid value for option '--factor': 'abc' is not an int
Usage: pngbloater bloat [-hV] [-f=<factor>] <file> <output>
...
```

## Built with

- [picocli](https://picocli.info/): command-line parsing and subcommands
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/): file name utilities
- [Maven](https://maven.apache.org/): build and dependency management

<small>drafted by Claude Sonnet 5.5</small>