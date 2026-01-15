# Exclusive Labels Plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/exclusive-labels-plugin/job/main/badge/icon)](https://ci.jenkins.io/job/Plugins/job/exclusive-labels-plugin/job/main/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/exclusive-label-plugin.svg)](https://plugins.jenkins.io/exclusive-label-plugin)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/exclusive-labels-plugin.svg?label=changelog)](https://github.com/jenkinsci/exclusive-labels-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/exclusive-label-plugin.svg?color=blue)](https://plugins.jenkins.io/exclusive-label-plugin)

Plugin enables to define exclusive agent labels

## Usage

Exclusive label enables specify which lablels are exclusive in Jenkins global configuration.

![Configuration screenshot](docs/config.png)

"Exclusive label" means that node with this label can be assigned only if user wants this label.

Example: an agent has three labels linux, hibernate, ipv6 and ipv6 is exclusive label. The agent cannot be asigned by label expression linux, hibernate, linux&&hibernate and so on because this labels do not match ipv6. The agent can be assigned by expression which match ipv6 too, for example: linux && ipv6.

![Queue screenshot](docs/queue.png)

## Configuration as code

```yaml
unclassified:
  exclusiveLabels:
    labelsInString: 'exclusive1 exclusive2'
```

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)
