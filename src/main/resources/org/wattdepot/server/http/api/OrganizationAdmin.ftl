<!DOCTYPE html>
<html>
<head>
<title>WattDepot Administration</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="/webroot/dist/css/bootstrap-theme.min.css">
<link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
<script src="/webroot/dist/js/less-1.3.0.min.js"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://code.jquery.com/jquery.js"></script>
<script src="/webroot/dist/js/bootstrap.min.js"></script>
<script src="/webroot/dist/js/wattdepot-admin.js"></script>
<script> 
$(function(){
  $("#modal-dialogs").load("/webroot/dist/dialogs.html"); 
  //$("#navigation-bar").load("/webroot/dist/navbar.html");
});
</script> 

</head>
<body>
<nav class="navbar navbar-default" role="navigation">
  <!-- Brand and toggle get grouped for better mobile display -->
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="#"><img src="/webroot/dist/wattdepot-logo.png"> WattDepot</a>
  </div>

  <!-- Collect the nav links, forms, and other content for toggling -->
  <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <ul class="nav navbar-nav">
      <li class="active"><a href="#">Definitions</a></li>
      <li><a href="/wattdepot/admin/summary/">Logs/Information/Summary</a></li>
 <!--      <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
        <ul class="dropdown-menu">
          <li><a href="#">Action</a></li>
          <li><a href="#">Another action</a></li>
          <li><a href="#">Something else here</a></li>
          <li class="divider"></li>
          <li><a href="#">Separated link</a></li>
          <li class="divider"></li>
          <li><a href="#">One more separated link</a></li>
        </ul>
      </li> -->
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
<!--       <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
        <ul class="dropdown-menu">
          <li><a href="#">Action</a></li>
          <li><a href="#">Another action</a></li>
          <li><a href="#">Something else here</a></li>
          <li class="divider"></li>
          <li><a href="#">Separated link</a></li>
        </ul>
      </li> -->
    </ul>
  </div><!-- /.navbar-collapse -->
</nav>

  <div id="modal-dialogs"></div>
  <div class="container">
  <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li><a id="depositories_tab_link" href="#depositories" data-toggle="tab">Depositories</a></li>
        <li><a id="sensors_tab_link" href="#sensors" data-toggle="tab">Sensors</a></li>
        <li><a id="sensorgroups_tab_link" href="#sensorgroups" data-toggle="tab">Sensor Groups</a></li>
        <li><a id="sensorprocesses_tab_link" href="#sensorprocesses" data-toggle="tab">Collector Process Definitions</a></li>
    </ul>
    <!-- Tab panes -->
    <div class="tab-content">
        <div class="tab-pane active" id="depositories">
            <div class="well">
                <table class="table">
                    <thead>
                      <tr><th colspan="5"><h3>Depositories</h3></th></tr>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Measurement Type</th>
                            <th style="width: 7px;"></th>
                            <th style="width: 7px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list depositories as d>
                        <tr><td>${d.id}</td><td>${d.name}</td><td><#if d.getMeasurementType()??>${d.measurementType.name}</#if></td>
                            <td>
                                <span class="glyphicon glyphicon-pencil" onclick="edit_depository_dialog(event, '${d.id}');"></span>
                            </td>
                            <td>
                                <span class="glyphicon glyphicon-remove" onclick="delete_depository_dialog(event, '${d.id}');"></span>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
                <button data-toggle="modal" data-target="#addDepositoryModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add Depository</a>
            </div>       
        </div>
        <div class="tab-pane" id="sensors">
            <div class="well">
                <table class="table">
                    <thead>
                      <tr><th colspan="5"><h3>Sensors</h3></th></tr>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>URI</th>
                            <th>Model</th>
                            <th>Properties</th>
                            <th style="width: 7px;"></th>
                            <th style="width: 7px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list sensors as s>
                        <tr><td>${s.id}</td>
                            <td>${s.name}</td>
                            <td>${s.uri}</td>
                            <td><#if s.getModelId()??>${s.modelId}</#if></td>
                            <td>[<#assign k = s.properties?size><#list s.properties as prop>{"${prop.key}":"${prop.value}"}<#if k != 1>,</#if><#assign k = k -1></#list>]</td>
                            <td>
                                <span class="glyphicon glyphicon-pencil" onclick="edit_sensor_dialog(event, '${s.id}');"></span>
                            </td>
                            <td>
                                <span class="glyphicon glyphicon-remove" onclick="delete_sensor_dialog(event, '${s.id}');"></span>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
                <button data-toggle="modal" data-target="#addSensorModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add Sensor</button>
            </div>   
            <div class="panel-group" id="accordion">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" data-parent="#accordion" href="#collapseModel">Sensor Models</a>
                        </h4>
                    </div>
                    <div id="collapseModel" class="panel-collapse collapse">
                        <div class="well">
                            <table class="table">
                                <thead>
                                    <tr><th colspan="5"><h3>Sensor Models</h3></th></tr>
                                    <tr>
                                        <th>Id</th>
                                        <th>Name</th>
                                        <th>Protocol</th>
                                        <th>Type</th>
                                        <th>Version</th>
                                        <th style="width: 7px;"></th>
                                        <th style="width: 7px;"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                <#list sensormodels as m>
                                    <tr><td>${m.id}</td><td>${m.name}</td><td>${m.protocol}</td><td>${m.type}</td><td>${m.version}</td>
                                        <td>
                                            <span class="glyphicon glyphicon-pencil" onclick="edit_model_dialog(event, '${m.id}');"></span>
                                        </td>
                                        <td>
                                            <span class="glyphicon glyphicon-remove" onclick="delete_model_dialog(event, '${m.id}');"></span>
                                        </td>
                                    </tr>
                                </#list>
                                </tbody>
                            </table>
                            <button data-toggle="modal" data-target="#addModelModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add Sensor Model</button>
                        </div>       
                    </div>
                </div>
            </div>
        </div>
        <div class="tab-pane" id="sensorgroups">
            <div class="well">
                <table class="table">
                    <thead>
                      <tr><th colspan="5"><h3>Sensor Groups</h3></th></tr>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Sensors</th>
                            <th style="width: 7px;"></th>
                            <th style="width: 7px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list sensorgroups as g>
                        <tr><td>${g.id}</td>
                            <td>${g.name}</td>
                            <td><#list g.sensors as s>${s} </#list></td>
                            <td>
                                <span class="glyphicon glyphicon-pencil" onclick="edit_sensorgroup_dialog(event, '${g.id}');"></span>
                            </td>
                            <td>
                                <span class="glyphicon glyphicon-remove" onclick="delete_sensorgroup_dialog(event, '${g.id}');"></span>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
                <button data-toggle="modal" data-target="#addSensorGroupModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add Sensor Group</button>
            </div>       
        </div>
        <div class="tab-pane" id="sensorprocesses">
            <div class="well">
                <table class="table">
                    <thead>
                      <tr><th colspan="5"><h3>Collector Process Definitions</h3></th></tr>
                        <tr>
                            <th>Id</th>
                            <th>Name</th>
                            <th>Sensor</th>
                            <th>Polling Interval</th>
                            <th>Depository</th>
                            <th>Properties</th>
                            <th style="width: 7px;"></th>
                            <th style="width: 7px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list sensorprocesses as p>
                        <tr><td>${p.id}</td>
                            <td>${p.name}</td>
                            <td>${p.sensorId}</td>
                            <td>${p.pollingInterval}</td>
                            <td>${p.depositoryId}</td>
                            <td>[<#assign k = p.properties?size><#list p.properties as prop>{"${prop.key}":"${prop.value}"}<#if k != 1>,</#if><#assign k = k -1></#list>]</td>
                            <td>
                                <span class="glyphicon glyphicon-pencil" onclick="edit_process_dialog(event, '${p.id}');"></span>
                            </td>
                            <td>
                                <span class="glyphicon glyphicon-remove" onclick="delete_process_dialog(event, '${p.id}');"></span>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
                <button data-toggle="modal" data-target="#addProcessModal" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-plus"></span> Add Collector Process Definition</button>
            </div>       
        </div>
    </div>  
    
    
  <!-- Add Sensor -->
  <div class="modal fade" id="addSensorModal" tabindex="-1" role="dialog" aria-labelledby="addSensorModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add/Edit Sensor</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form>
              <input type="hidden" name="sensor_id" value="">            
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_id">Sensor Name</label>
                <div class="col-md-9">
                  <input type="text" name="sensor_name" class="form-control">
                  <p class="help-block">Sensor names must be unique.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_uri">Sensor URI</label>
                <div class="col-md-9">
                  <input type="text" name="sensor_uri" class="form-control">
                  <p class="help-block">The URI to contact the sensor.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_model">Model</label>
                <div class="col-md-9">
                  <select class="form-control" name="sensor_model">
                  <#list sensormodels as sm>
                    <option value="${sm.id}">${sm.name}</option>
                  </#list>
                  </select>
                  <p class="help-block">Select the model for the sensor.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Properties</label>
                <div class="col-sm-9">
                  <div id="sensor_properties">
                  </div>
                  <p class="help-block">List of the Sensor's Properties.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
            <button class="btn-xs btn-success" data-toggle="collapse" data-target="#newLocationForm"><span class="glyphicon glyphicon-plus"></span> Location</button>                
            <div id="newLocationForm" class="collapse"> 
              <form>
                <div class="form-group">
                  <label class="col-md-3 control-label">Location Name</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_location_id" class="form-control">
                    <p class="help-block">The unique name of the location.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Latitude</label>
                  <div class="col-md-9">
                    <input type="number" name="inline_location_latitude" class="form-control">
                    <p class="help-block">Enter the latitude.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Longitude</label>
                  <div class="col-md-9">
                    <input type="number" name="inline_location_longitude" class="form-control">
                    <p class="help-block"></p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Altitude</label>
                  <div class="col-md-9">
                    <input type="number" name="inline_location_altitude" class="form-control">
                    <p class="help-block"></p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Description</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_location_description" class="form-control">
                    <p class="help-block"></p>
                  </div>
                </div>
                <div class="clearfix"></div>
              </form>
              <button type="button" class="btn-sm btn-primary"
                        onclick="putNewInlineLocation();">Save Location</button>
              <p></p>
            </div>
            <button class="btn-xs btn-success" data-toggle="collapse" data-target="#newModelForm"><span class="glyphicon glyphicon-plus"></span> Model</button>
            <div id="newModelForm" class="collapse"> 
              <form>
                <div class="form-group">
                  <label class="col-md-3 control-label">Sensor Model Name</label> 
                  <div class="col-md-9">
                    <input type="text" name="inline_model_id" class="form-control">
                    <p class="help-block">Unique model name.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Protocol</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_model_protocol" class="form-control">
                    <p class="help-block">The protocol used by the sensor.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Type</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_model_type" class="form-control">
                    <p class="help-block">The type of the sensor.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Version</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_model_version" class="form-control">
                    <p class="help-block">The version.</p>
                  </div>
                </div>
                <div class="clearfix"></div>
              </form>
              <button type="button" class="btn-sm btn-primary"
                        onclick="putNewInlineModel();">Save Model</button>
              <p></p>
            </div>                
            <button class="btn-xs btn-success" data-toggle="collapse" data-target="#newSensorPropertyForm"><span class="glyphicon glyphicon-plus"></span> Property</button>                
            <div id="newSensorPropertyForm" class="collapse">
              <form>
                <div class="form-group">
                  <label class="col-md-3 control-label">Key</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_sensor_key" class="form-control">
                    <p class="help-block">The property key.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Value</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_sensor_value" class="form-control">
                    <p class="help-block">The property value.</p>
                  </div>
                </div>
              </form>
              <button type="button" class="btn-sm btn-primary"
                      onclick="putNewInlineSensorProperty();">Add Property</button>
              <p></p>
            </div>
                
            </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewSensor();">Save changes</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->    

  <!-- Add Sensor Group -->
  <div class="modal fade" id="addSensorGroupModal" tabindex="-1" role="dialog" aria-labelledby="addSensorGroupModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add/Edit Sensor Group</h4>
        </div>
        <div class="modal-body">
            <div class="container">
                <form>
                <div class="form-group">
                        <label class="col-md-3 control-label" for="sensorgroup_name">Group Name</label>
                        <div class="col-md-9">
                        <input type="text" name="sensorgroup_name" class="form-control"><p class="help-block"></p>
                        </div>
                </div>
                <div class="form-group">
                            <label class="col-md-3 control-label" for="groupsensors">Sensors</label>
                            <div class="col-md-9">
                            <select class="form-control" name="groupsensors" multiple="multiple">
                            <#list sensors as s>
                                <option value="${s.id}">${s.id}</option>
                            </#list>
                            </select>
                            <p class="help-block"></p>
                            </div>
                </div>
                <div class="clearfix"></div>
                </form>
            </div>                
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewSensorGroup();">Save changes</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->    

  <!-- Add Collector Process Defintion -->
  <div class="modal fade" id="addProcessModal" tabindex="-1" role="dialog" aria-labelledby="addProcessModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add/Edit Collector Process Definition</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form>
              <input type="hidden" name="meta_id" value="">
              <div class="form-group">
                 <label class="col-sm-3 control-label">Collector Name</label>
                 <div class="col-sm-9">
                   <input class="form-control" type="text" name="sensorprocess_name" class="form-control">
                   <p class="help-block">Unique name for the definition.</p>
                 </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Sensor</label>
                <div class="col-sm-9">
                  <select class="form-control" name="process_sensor">
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>
                  <p class="help-block">Select the sensor making measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Polling Interval</label>
                <div class="col-sm-9">
                  <input class="form-control" type="number" name="sensorprocess_polling" class="form-control">
                  <p class="help-block">Number of seconds between measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Depository</label>
                <div class="col-sm-9">
                  <select class="form-control" name="process_depository">
                  <#list depositories as d>
                    <option value="${d.id}">${d.name}</option>
                  </#list>
                  </select>
                  <p class="help-block">Select the depository to store the measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Properties</label>
                <div class="col-sm-9">
                  <div id="metadata_properties">
                  </div>
                  <p class="help-block">List of the Collector's Properties.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
            <button class="btn-xs btn-success" data-toggle="collapse" data-target="#newMetaPropertyForm"><span class="glyphicon glyphicon-plus"></span> Property</button>                
            <div id="newMetaPropertyForm" class="collapse">
              <form>
                <div class="form-group">
                  <label class="col-md-3 control-label">Key</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_meta_key" class="form-control">
                    <p class="help-block">The property key.</p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Value</label>
                  <div class="col-md-9">
                    <input type="text" name="inline_meta_value" class="form-control">
                    <p class="help-block">The property value.</p>
                  </div>
                </div>
              </form>
              <button type="button" class="btn-sm btn-primary"
                      onclick="putNewInlineMetaProperty();">Add Property</button>
              <p></p>
            </div>
          </div>
        </div> <!-- /.modal-body -->                
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewProcess();">Save changes</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->    

<!-- Add Depository -->
<div class="modal fade" id="addDepositoryModal" tabindex="-1"
    role="dialog" aria-labelledby="addDepositoryModalLabel"
    aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                    aria-hidden="true">&times;</button>
                <h4 class="modal-title">Add/Edit Depository</h4>
            </div>
            <div class="modal-body">
                <div class="container">
                    <form>
                        <div class="form-group">
                            <label class="col-md-3 control-label">Depository
                                Name</label>
                            <div class="col-md-9">
                                <input type="text"
                                    name="depository_name"
                                    class="form-control">
                                <p class="help-block">Unique name.</p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-3 control-label">Depository
                                Measurement Type</label>
                            <div class="col-md-9">
                                <select class="form-control" name="depository_type">
                                <#list measurementtypes as mt>
                                    <option value="${mt.id}">${mt.name}</option>
                                </#list>
                                </select>
                                <p class="help-block">The type of measurement the depository stores.</p>
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default"
                    data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary"
                    onclick="putNewDepository();">Save changes</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


</div>
<script>
$(document).ready(function () {
    var selected_tab = getCookie("selected-tab");
    if (selected_tab != null) {
        $('#' + selected_tab + '_tab_link').tab('show');
    }
    
});

var GROUPID = "${orgId}";
var DEPOSITORIES = {};
<#list depositories as d>
DEPOSITORIES["${d.id}"] = {"id": "${d.id}", "name": "${d.name}", "measurementType": "${d.measurementType}", "organizationId": "${d.organizationId}"};
</#list>
var MODELS = {};
<#list sensormodels as m>
MODELS["${m.id}"] = {"id": "${m.id}", "name": "${m.name}", "protocol": "${m.protocol}", "type": "${m.type}", "version": "${m.version}"};
</#list>
var SENSORS = {};
<#list sensors as s>
SENSORS["${s.id}"] = {"id": "${s.id}", "name": "${s.name}", "uri": "${s.uri}",  "modelId": "<#if s.getModelId()??>${s.modelId}</#if>", "organizationId": "${s.organizationId}", "properties" : [<#assign k = s.properties?size><#list s.properties as p>{"key":"${p.key}", "value":"${p.value}"}<#if k != 1>,</#if><#assign k = k -1></#list>]};
</#list>
var SENSORGROUPS = {};
<#list sensorgroups as sg>
SENSORGROUPS["${sg.id}"] = {"id": "${sg.id}", "name": "${sg.name}", "sensors": [
<#assign sgLen = sg.sensors?size>
<#list sg.sensors as s>
{"id": "${s}"}<#if sgLen != 1>,</#if><#assign sgLen = sgLen - 1>
</#list>
], "organizationId": "${sg.organizationId}"};
</#list>
var SENSORPROCESSES = {};
<#list sensorprocesses as sp>
SENSORPROCESSES["${sp.id}"] = {"id": "${sp.id}", "name": "${sp.name}",  "sensorId": "${sp.sensorId}", "pollingInterval": ${sp.pollingInterval}, "depositoryId": "${sp.depositoryId}", "organizationId": "${sp.organizationId}", "properties" : [<#assign k = sp.properties?size><#list sp.properties as p>{"key":"${p.key}", "value":"${p.value}"}<#if k != 1>,</#if><#assign k = k -1></#list>]};
</#list>
var MEASUREMENTTYPES = {};
<#list measurementtypes as mt>
MEASUREMENTTYPES["${mt.id}"] = {"id": "${mt.id}", "name": "${mt.name}", "units": "${mt.units}"};
</#list>
</script>
</body>
</html>