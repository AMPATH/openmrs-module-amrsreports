<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Locations" otherwise="/login.htm" redirect="/module/amrsreports/cccNumbers.list"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery.dataTables.min.js"/>

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/dataTables_jui.css"/>

<%@ include file="localHeader.jsp" %>

<style>
    .bold { font-weight: bold; }
    .hidden { display: none; }
    .centered { text-align: center; }
    .visualPadding { margin-bottom: 1em; }

    #authDialog input { width: 80%; }

    /** loading widget **/
    .message {border: 1px solid #bbb; padding: 0.25em 0.5em; font-size: 0.85em; margin-left: 0.5em; background: #eee; position: relative; top: -2px; }
    .message img { top: 5px; position: relative; }
</style>

<script>
    var productionServer = "${productionServerURL}";
    var currentFacility = null;
    var currentPatients = null;
    var currentSerial = null;

    // TODO make auth work; this should start off null, but setting to something so auth is skipped
    // var authToken = null;
    var authToken = "fake";

    // change this to true if AJAX will work
    var usingAJAX = false;

    $j(document).ready(function(){

        // initialize the facility table
        $j("#facilityTable").dataTable({
            bJQueryUI: true,
            bAutoWidth: true,
            sScrollXInner: "110%",
            sPaginationType: "full_numbers"
        });

        // initialize the confirmation dialog
        $j("#confirmDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            width: "400px",
            buttons: {
                "Assign Identifiers": function() {

                    // check for authentication
                    if (authToken == null) {
                        $j("input[name=username]").val("");
                        $j("input[name=password]").val("");
                        $j("#authDialog").dialog("open");
                        $j("input[name=username]").focus();
                    } else {

                        // begin working on identifiers
                        assignIdentifiers();
                    }
                },
                Cancel: function() {
                    $j(this).dialog("close");
                }
            }
        });

        // initialize the authorization dialog
        $j("#authDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            width: "400px",
            buttons: {
                "Authenticate": function() {

                    // clear error notification
                    $j("#authError").fadeOut("fast");

                    // initialize restClient if needed
                    if (authToken == null) {
                        var uname = $j("input[name=username]").val();
                        var pword = $j("input[name=password]").val();

                        $j.ajax({
//                            beforeSend : function(xhr) {
//                                var base64 = btoa(uname + ":" + pword);
//                                xhr.setRequestHeader("Authorization", "Basic " + base64);
//                            },
                            contentType: 'application/javascript',
                            crossDomain: true,
                            dataType: 'jsonp',
                            username: uname,
                            password: pword,
                            type: 'get',
                            url: productionServer + '/ws/rest/v1/session',
                            success: function(sid) {
                                if (sid.authenticated) {

                                    // set the token
                                    authToken = sid.sessionId;

                                    // close the dialog
                                    $j("#authDialog").dialog("close");

                                    // begin working on identifiers
                                    assignIdentifiers();
                                } else {

                                    // show the warning
                                    $j("#authError").fadeIn("fast", function(){
                                        alert(sid.authenticated);
                                        $j("#input[name=username]").focus();
                                    });
                                }
                            },
                            error: function(err) {
                                $j("#authError").fadeIn();
                                alert(err.responseText);
                                $j("input[name=username]").focus();
                            }
                        });

                    }
                },
                Cancel: function() {
                    // clear error notification
                    $j("#authError").fadeOut("fast");

                    // close the dialog
                    $j(this).dialog("close");
                }
            }
        });

        // initialize the confirmation dialog
        $j("#listDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            width: "400px",
            buttons: {
                "Generate Identifiers": function() {

                    // validate
                    var lastSerial = parseInt($j("#listSerialNumber").html());
                    var startingSerial = parseInt($j("#startingSerial").val());

                    // see if the starting serial number is a number
                    if (isNaN(startingSerial)) {
                        $j("#listSerialError").html("The starting serial should be a number.")
                        $j("#listSerialError").fadeIn();
                        $j("#startingSerial").focus();
                        $j("#startingSerial").select();
                        return;
                    }

                    // ensure the starting serial number comes after the last one assigned
                    if (startingSerial <= lastSerial) {
                        $j("#listSerialError").html("The starting serial should be after the last assigned one.")
                        $j("#listSerialError").fadeIn();
                        $j("#startingSerial").focus();
                        $j("#startingSerial").select();
                        return;
                    }

                    // hide the serial error message if all is well
                    $j("#listSerialError").fadeOut();

                    // ensure a size is selected
                    var count = $j("#identifierCount").val();

                    if (count == null) {
                        $j("#listCountError").html("You must select a size.")
                        $j("#listCountError").fadeIn();
                        $j("#identifierCount").focus();
                        return;
                    }

                    $j("#listCountError").fadeOut();

                    // trigger the download
                    window.location.href = "downloadCCCNumberList.htm?" +
                            $j.param({
                                facilityId: currentFacility,
                                startingSerial: startingSerial,
                                count: count
                            });
                    $j(this).dialog("close");
                },
                Cancel: function() {
                    $j(this).dialog("close");
                }
            }
        });

        // set the trigger for assign buttons
        $j("button.assign").live("click", function(event){
            event.preventDefault();

            // clear the variable spans
            $j("#confirmDialog span.variable").html("");
            $j("#confirmDialog span.variable").hide();

            // show the loading messages
            $j("#confirmDialog span.loading").show();

            // update the dialog variables from a DWR call
            currentFacility = $j(this).attr("facilityId");
            currentSerial = $j(this).attr("serial");

            // populate the facility name
            $j("#confirmFacilityName").html($j(this).attr("facilityName"));
            $j("#confirmFacilityName").show();

            // open the assignment modal dialog
            $j("#confirmDialog").dialog("open");

            // populate the missing patient count
            DWRAmrsReportService.getPatientUuidsMissingCCCNumbersInFacility(currentFacility, function(patients) {
                currentPatients = patients;
                $j("#confirmCountValue").html(patients.length);
                $j("#confirmCount span.loading").fadeOut("fast", function(){
                    $j("#confirmCountValue").fadeIn("fast");
                });
            });
        });

        // set the trigger for assign buttons
        $j("button.list").live("click", function(event){
            event.preventDefault();

            currentFacility = $j(this).attr("facilityId");

            // clear the variable spans
            $j("#listDialog span.variable").html("");

            // populate the facility name
            $j("#listFacilityName").html($j(this).attr("facilityName"));

            // populate the serial number
            $j("#listSerialNumber").html($j(this).attr("serial"));

            // clear some information before showing the dialog
            $j("#listDialog .error").html("");
            $j("#listDialog .error").hide();
            $j("#startingSerial").val("");

            // open the assignment modal dialog
            $j("#listDialog").dialog("open");

            // focus on the starting serial entry
            $j("#startingSerial").select();
        });

    });

    function assignIdentifiers() {
        if (usingAJAX) {
            assignIdentifiersAJAX();
        } else {
            assignIdentifiersSQL();
        }
    }

    function assignIdentifiersSQL() {
        // use a controller to generate SQL for this facility's missing CCC numbers and download it
        window.location.href = "downloadCCCNumberSQL.htm?facilityId=" + currentFacility;
        $j("#confirmDialog").dialog("close");
    }

    function assignIdentifiersAJAX() {
        // get the facility code
        DWRAmrsReportService.getFacilityCode(currentFacility, function(code) {

            // update dialog display
            $j("#assignDialog #currentIdentifier").html("0");
            $j("#assignDialog #totalIdentifiers").html(currentPatients.length);

            // open the status dialog
            $j("#assignProgress").fadeIn();

            // loop through patients
            currentPatients.forEach(function(patientUuid, index, array) {

                // increase the current serial
                currentSerial++;

                // create the new identifier
                var identifier = code + "-" + String("00000" + currentSerial).slice(-5);

                // send it off for adding
                addIdentifier(patientUuid, identifier);
            });
        });
    }

    function addIdentifierAJAX(patientUuid, identifier) {
        DWRAmrsReportService.getPreARTEnrollmentLocationUuidForPatientUuid(patientUuid, function(locationUuid) {
            if (locationUuid != null) {
                $j.ajax({
                    type: 'post',
                    url: productionServer + '/ws/rest/v1/patient/' + patientUuid + '/identifier',
                    jsessionid: authToken,
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    data: JSON.stringify({
                        "identifier": identifier,
                        "identifierType": {
                            "uuid": "${cccIdentifierTypeUuid}"
                        },
                        "location": {
                            "uuid": locationUuid
                        },
                        "preferred": false
                    }),
                    success: function(sid) {
                        alert("omg");
                    },
                    error: function(err) {
                        alert("meh");
                    }
                });
            }
        });
    }

</script>

<h2>Manage CCC Numbers</h2>

<div class="visualPadding">
    Search through the table below to find a facility and select the "Assign" button to find how many identifiers
    need to be assigned at this time and perform the assignment.  The result of assigning identifiers at this time is
    a downloaded SQL file.  This must be handed off to a representative in the IT department for importing into the
    Production database.
</div>

<table id="facilityTable" class="display">
    <thead>
        <tr>
            <th> Action </th>
            <th> <spring:message code="general.name" /> </th>
            <th> Facility Code </th>
            <th> Highest Serial Number </th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="facility" items="${facilities}" varStatus="status">
        <tr>
            <td valign="top">
                <button class="assign" facilityId="${facility.facilityId}" serial="${serials[facility.facilityId]}" facilityName="${facility.name}">Assign to Gap</button>
                <button class="list" facilityId="${facility.facilityId}" serial="${serials[facility.facilityId]}" facilityName="${facility.name}">Generate List</button>
            </td>
            <td valign="top" style="white-space:nowrap">${facility.name}</td>
            <td valign="top">${facility.code}</td>
            <td valign="top">${serials[facility.facilityId]}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div id="confirmDialog">
    <div class="visualPadding" id="confirmFacility">
        <span class="bold">Facility:</span>
        <span class="variable" id="confirmFacilityName"></span>
    </div>
    <div class="visualPadding" id="confirmCount">
        <span class="bold">Missing identifiers:</span>
        <span class="hidden message loading">
            <img src="${pageContext.request.contextPath}/images/loading.gif"/>
            <spring:message code="general.loading"/>
        </span>
        <span class="variable" id="confirmCountValue"></span>
    </div>
    <div class="visualPadding description">
        Clicking "Assign Identifiers" will create CCC Number identifiers for all of the patients for this facility
        who do not already have CCC Numbers assigned but should.  If you are not sure about doing this, please cancel
        and consult with an administrator.
    </div>
    <div class="visualPadding hidden" id="assignProgress">
        <span class="bold">Assigning identifier:</span>
        <span class="variable" id="currentIdentifier"></span>
        /
        <span class="variable" id="totalIdentifiers"></span>
    </div>
</div>

<div id="authDialog">
    <div class="visualPadding hidden error" id="authError">
        There was an error authenticating.  Please try again.
    </div>
    <div class="visualPadding description">
        Enter your username and password for the Production server.  This is necessary for creating new identifiers
        for patients in OpenMRS.
    </div>
    <div class="visualPadding centered">
        <input type="text" name="username" placeholder="Username"/>
    </div>
    <div class="visualPadding centered">
        <input type="password" name="password" placeholder="Password"/>
    </div>
    <div class="hidden" id="authenticating">
        Authenticating ...
    </div>
</div>

<div id="listDialog">
    <form id="listForm">
        <div class="visualPadding" id="listFacility">
            <span class="bold">Facility:</span>
            <span class="variable" id="listFacilityName"></span>
        </div>
        <div class="visualPadding" id="listSerial">
            <span class="bold">Last assigned serial number:</span>
            <span class="variable" id="listSerialNumber"></span>
        </div>
        <div class="visualPadding description">
            Set the starting serial number and amount of identifiers to generate.  Clicking "Generate" will then start
            a download of a text file with the relevant identifiers.
        </div>
        <div class="visualPadding hidden error" id="listSerialError">
        </div>
        <div class="visualPadding" id="listStart">
            <label for="startingSerial" class="bold">Starting Serial:</label>
            <input type="text" id="startingSerial"/>
        </div>
        <div class="visualPadding hidden error" id="listCountError">
        </div>
        <div class="visualPadding" id="listCount">
            <label for="identifierCount" class="bold">Number of Identifiers:</label>
            <select id="identifierCount" size="4">
                <option>100</option>
                <option>500</option>
                <option selected="true">1000</option>
                <option>2000</option>
            </select>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
