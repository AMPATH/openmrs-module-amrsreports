<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Add Patient Identifiers" otherwise="/login.htm" redirect="/module/amrsreports/cccNumbers.list"/>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/jquery.dataTables.min.js"/>

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/dataTables_jui.css"/>

<style>
    .bold { font-weight: bold; }
    .hidden { display: none; }
    .visualPadding { margin-bottom: 1em; }

    /* loading */
    .message {
        border: 1px solid #bbb;
        padding: 0.25em 0.5em;
        font-size: 0.85em;
        margin-left: 0.5em;
        background: #eee;
        position: relative;
        top: -2px;
    }
    .message img { top: 5px; position: relative; }

</style>

<script>
    $j(document).ready(function(){

        $j("#facilityTable").dataTable({
            bJQueryUI: true,
            bAutoWidth: true,
            sScrollXInner: "110%",
            sPaginationType: "full_numbers"
        });

        $j("#assignDialog").dialog({
            autoOpen: false,
            resizable: false,
            modal: true,
            width: "400px",
            buttons: {
                "Assign Identifiers": function() {
                    $j( this ).dialog( "close" );
                },
                Cancel: function() {
                    $j( this ).dialog( "close" );
                }
            }
        });

        $j("button.assign").click(function(event){
            event.preventDefault();

            // clear the variable spans
            $j("#assignDialog span.variable").html("");
            $j("#assignDialog span.variable").hide();

            // show the loading messages
            $j("#assignDialog span.loading").show();

            // open the assignment modal dialog
            $j("#assignDialog").dialog("open");

            // update the dialog variables from a DWR call
            var facilityId = $j(this).attr("facilityId");

            // populate the facility name
            DWRAmrsReportService.getFacilityName(facilityId, function(name) {
                $j("#assignFacilityName").html(name);
                $j("#assignFacility span.loading").fadeOut("fast", function(){
                    $j("#assignFacilityName").fadeIn("fast");
                });
            });

            // populate the missing patient count
            DWRAmrsReportService.getPatientCountMissingCCCNumbersInFacility(facilityId, function(c) {
                $j("#assignCountValue").html(c);
                $j("#assignCount span.loading").fadeOut("fast", function(){
                    $j("#assignCountValue").fadeIn("fast");
                });
            });
        });

    });
</script>

<%@ include file="localHeader.jsp" %>

<h2>Manage CCC Numbers</h2>

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
            <td valign="top"><button class="assign" facilityId="${facility.facilityId}">Assign</button></td>
            <td valign="top" style="white-space:nowrap">${facility.name}</td>
            <td valign="top">${facility.code}</td>
            <td valign="top">${serials[facility.facilityId]}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div id="assignDialog">
    <div class="visualPadding" id="assignFacility">
        <span class="bold">Facility:</span>
        <span class="hidden message loading">
            <img src="${pageContext.request.contextPath}/images/loading.gif"/>
            <spring:message code="general.loading"/>
        </span>
        <span class="variable" id="assignFacilityName"></span>
    </div>
    <div class="visualPadding" id="assignCount">
        <span class="bold">Missing identifiers:</span>
        <span class="hidden message loading">
            <img src="${pageContext.request.contextPath}/images/loading.gif"/>
            <spring:message code="general.loading"/>
        </span>
        <span class="variable" id="assignCountValue"></span>
    </div>
    <div class="description">
        Clicking "Assign Identifiers" will create CCC Number identifiers for all of the patients for this facility
        who do not already have CCC Numbers assigned but should.  If you are not sure about doing this, please cancel
        and consult with an administrator.
    </div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
