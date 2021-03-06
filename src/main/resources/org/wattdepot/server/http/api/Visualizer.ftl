<!DOCTYPE html>
<html>
<head>
    <title>WattDepot Organization Measurement Visualization</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
    <!-- Optional theme -->
    <link rel="stylesheet" href="/webroot/dist/css/bootstrap-theme.min.css">
    <link rel="stylesheet"
          href="/webroot/bower_components/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="/webroot/dist/css/normalize.css">
    <link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
    <script src="/webroot/dist/js/less-1.3.0.min.js"></script>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="/webroot/bower_components/jquery/dist/jquery.js"></script>
    <script src="/webroot/bower_components/moment/min/moment.min.js"></script>
    <script src="/webroot/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script
            src="/webroot/bower_components/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>
    <script src="/webroot/dist/js/wattdepot-permalink.js"></script>
    <script src="/webroot/dist/js/wattdepot-visualizer.js"></script>
    <script src="/webroot/dist/js/org.wattdepot.client.js"></script>
    <!--Load the Google AJAX API-->
    <script type='text/javascript' src='http://www.google.com/jsapi'></script>

    <script>
        // Load the Visualization API and Annotated Timeline visualization
        google.load('visualization', '1.0', {
            'packages': ['corechart']
        });

        // Set a callback to run when the API is loaded.
        google.setOnLoadCallback(function () {
            loaded = true;
        });

    </script>

</head>
<body onload="loadPage()">
<nav class="navbar navbar-default" role="navigation">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#"><img src="/webroot/dist/wattdepot-logo.png"></a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="nav navbar-nav">
            <li><a href="/wattdepot/${orgId}/">Settings</a></li>
            <li><a href="/wattdepot/${orgId}/summary/">Summary</a></li>
            <li class="active"><a href="/wattdepot/${orgId}/visualize/">Chart</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="#">${orgId}</a></li>
        </ul>
    </div>
    <!-- /.navbar-collapse -->
</nav>

<div class="container">
    <!-- Visualizer Control Panel -->
    <div id="visualizerControlPanel">
        <div id="formLabels" class="row">
            <div class="col-xs-3 row">Depository:</div>
            <div class="col-xs-2">Sensor:</div>
            <div class="col-xs-2">Start Time:</div>
            <div class="col-xs-2">End Time:</div>
            <div class="col-xs-3 row">
                <div class="col-xs-5">Type:</div>
                <div class="col-xs-5">Frequency:</div>
                <div class="col-xs-2">CSV:</div>
            </div>
        </div>
        <div id="visualizerFormsDiv">
        </div>
        <div id="controlPanelButtonRow" class="row">
            <div class="col-xs-8 control-group">
                <button id="visualizeButton" class="col-xs-2 btn btn-primary" onclick="visualize();" diabled>
                    Visualize!
                </button>
                <div class="col-xs-10 control-group" id="permalink" style="display:none">Share URL:<input id="linkSpace"
                                                                                                          class="input-large"
                                                                                                          type="text"
                                                                                                          placeholder="Your Permalink"
                                                                                                          style="width:80%">
                </div>
            </div>
            <div class="col-xs-3"></div>
            <div class="col-xs-1 control-group">
                <button id="addRowButton" class="btn btn-primary" onclick="addRow();" data-toggle="tooltip"
                        data-placement="left" title="Add another row"><span class="glyphicon glyphicon-plus"></span>
                </button>
            </div>
        </div>
    </div>
    <div id='chartDiv' style='height: 500px;'></div>
</div>
<div class="modal fade" id="progressWindow" tabindex="-1"
     role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Visualizing...</h3>
            </div>
            <div class="modal-body">
                <div id="visualizeProgress" class="progress">
                    <div id="visualizeProgressBar" class="bar" style="width: 0%;"></div>
                </div>
                <p id="progressLabel"></p>

                <p id="timeLabel"></p>
            </div>
            <div class="modal-footer">
                <button class="btn btn-primary" type="button" id="cancelQuery" data-dismiss="modal">Cancel</button>
                <script>
                    $('#cancelQuery').click(function () {
                        stopQueries();
                        $('#visualizeButton').attr("disabled", false);
                        $('#visualizeButton').empty();
                        $('#visualizeButton').append("Visualize!");
                    })
                </script>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<script>
    var server = window.location.protocol + "//" + window.location.host + "/wattdepot/";

    var ORGID = "${orgId}";
    var DEPOSITORIES = {};
    <#list depositories as d>
    DEPOSITORIES["${d.id}"] = {
        "id": "${d.id}",
        "name": "${d.name}",
        "measurementType": "${d.measurementType.id}",
        "typeString": "${d.measurementType.units}",
        "organizationId": "${d.organizationId}"
    };
    </#list>
    var SENSORS = {};
    <#list sensors as s>
    SENSORS["${s.id}"] = {
        "id": "${s.id}",
        "name": "${s.name}",
        "uri": "${s.uri}",
        "modelId": "<#if s.getModelId()??>${s.modelId}</#if>",
        "organizationId": "${s.organizationId}",
        "properties": [<#assign k = s.properties?size><#list s.properties as p>{
            "key": "${p.key}",
            "value": "${p.value}"
        }<#if k != 1>,</#if><#assign k = k -1></#list>]
    };
    </#list>
    var SENSORGROUPS = {};
    <#list sensorgroups as sg>
    SENSORGROUPS["${sg.id}"] = {
        "id": "${sg.id}", "name": "${sg.name}", "sensors": [
            <#assign sgLen = sg.sensors?size>
            <#list sg.sensors as s>
                {"id": "${s}"}<#if sgLen != 1>,</#if><#assign sgLen = sgLen - 1>
            </#list>
        ], "organizationId": "${sg.organizationId}"
    };
    </#list>
    var DEPO_SENSORS = {};

    <#assign keys = depoSensors?keys>
    <#list keys as key>
    DEPO_SENSORS["${key}"] = [
        <#assign len = depoSensors[key]?size>
        <#list depoSensors[key] as s>
            "${s.id}"<#if len != 1>, </#if><#assign len = len - 1>
        </#list>
    ]
    </#list>
    var DEPO_SENSOR_INFO = {};
    <#assign depos = depotSensorInfo?keys>
    <#list depos as dep>
    DEPO_SENSOR_INFO["${dep}"] = {};
        <#assign sensorIds = depotSensorInfo[dep]?keys>
        <#list sensorIds as id>
        DEPO_SENSOR_INFO["${dep}"]["${id}"] = {
            "earliest": "${depotSensorInfo[dep][id][0]?datetime?iso_local_ms}",
            "latest": "${depotSensorInfo[dep][id][1]?datetime?iso_local_ms}"
        };

        </#list>
    </#list>
</script>
</body>
</html>