{{- define "discovery.url" -}}
{{- printf "%s:%d" .Values.global.discovery.serviceName (.Values.global.discovery.port | int) -}}
{{- end -}}

{{- define "postgres.url" -}}
{{- printf "%s:%d" .Values.global.postgres.host (.Values.global.postgres.port | int) -}}
{{- end -}}

{{- define "mongodb.url" -}}
{{- printf "%s:%d" .Values.global.mongodb.host (.Values.global.mongodb.port | int) -}}
{{- end -}}

{{- define "rabbitmq.url" -}}
{{- printf "%s:%d" .Values.global.rabbitmq.host (.Values.global.rabbitmq.port | int) -}}
{{- end -}}


{{- define "common.wait.discovery.init.container" -}}
- name: "wait-for-discovery"
  image: "curlimages/curl:latest"
  imagePullPolicy: IfNotPresent
  command: [ 'sh', '-c',
             'until curl -sSf --connect-timeout 3 {{ include "discovery.url" . }} > /dev/null
              && echo Service available!;
              do echo Service not available;
              sleep 0.5; done;'
  ]
{{- end -}}

{{- define "common.wait.postgres.init.container" -}}
- name: "wait-for-postgres"
  image: "curlimages/curl:latest"
  imagePullPolicy: IfNotPresent
  command: [ 'sh', '-c',
             'until nc -vz -w 3 {{.Values.global.postgres.host}} {{.Values.global.postgres.port}} > /dev/null
              && echo Service available!;
              do echo Service not available;
              sleep 0.5; done;'
  ]
{{- end -}}

{{- define "common.wait.rabbitmq.init.container" -}}
- name: "wait-for-rabbitmq"
  image: "curlimages/curl:latest"
  imagePullPolicy: IfNotPresent
  command: [ 'sh', '-c',
             'until nc -vz -w 3 {{.Values.global.rabbitmq.host}} {{.Values.global.rabbitmq.port}} > /dev/null
              && echo Service available!;
              do echo Service not available;
              sleep 0.5; done;'
  ]
{{- end -}}

{{- define "common.wait.mongodb.init.container" -}}
- name: "wait-for-mongodb"
  image: "curlimages/curl:latest"
  imagePullPolicy: IfNotPresent
  command: [ 'sh', '-c',
             'until nc -vz -w 3 {{.Values.global.mongodb.host}} {{.Values.global.mongodb.port}} > /dev/null
              && echo Service available!;
              do echo Service not available;
              sleep 0.5; done;'
  ]
{{- end -}}

{{- define "main.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "main.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "main.labels" -}}
helm.sh/chart: {{ include "main.chart" . }}
{{ include "main.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "main.selectorLabels" -}}
app.kubernetes.io/name: {{ include "main.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
