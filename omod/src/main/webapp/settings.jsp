<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Global Properties" otherwise="/login.htm" redirect="/module/amrsreports/settings.form"/>

<%@ include file="localHeader.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWRAdministrationService.js" />

<style>
    span.saved { font-style: italic; color: green; display: none; }
    div#taskMessage { font-style: italic; }
    div#choices, div#buttons { margin-top: 1em; }
    b.boxHeader { padding-left: 0.5em; }
</style>

<script>

    function enableSaveFor(wrapper) {
        $j("#" + wrapper + " input[name=save]").fadeIn();
    }

    function saveGPFor(wrapper) {
        var property = $j("#" + wrapper + " [name=property]").val();
        var value = $j("#" + wrapper + " [name=value]").val();

        DWRAdministrationService.setGlobalProperty(property, value, function(){
            $j("#" + wrapper + " input[name=save]").fadeOut("fast", function(){
                $j("#" + wrapper + " span.saved").fadeIn("fast", function(){
                    $j("#" + wrapper + " span.saved").fadeOut(2000);
                });
            });
        });

        return false;
    }

    function clearTaskMessage() {
        $j("#taskMessage").fadeOut("fast", function(){
            $j("#taskMessage span.message").html("");
        });
    }

    function showTaskMessage(message) {
        $j("#taskMessage span.message").html(message);
        $j("#taskMessage").fadeIn();
    }

    function getTaskRunnerStatus() {
        DWRAmrsReportService.getTaskRunnerStatus(function(result){
            if (!result)
                clearTaskMessage();
            else
                showTaskMessage(result);
        });
    }

    $j(document).ready(function() {

        // hide save buttons
        $j("input[name=save]").hide();

        // hide task message
        $j("#taskMessage").hide();

        // action event for start button
        $j("#startTask").click(function(event){
            event.preventDefault();

            var taskName = $j("input[name=taskName]:checked").val();
            if (taskName === "")
                return;

            DWRAmrsReportService.startTaskRunner(taskName, function(result) {
                showTaskMessage(result);
                $j("input[name=taskName]:checked").removeAttr("checked");
            });
        });

        // action event for stop button
        $j("#stopTask").click(function(event){
            event.preventDefault();

            DWRAmrsReportService.stopTaskRunner(function(result){
                if (!result)
                    clearTaskMessage();
                else
                    showTaskMessage(result);
            });
        });

        // get the latest status to pre-populate the page
        getTaskRunnerStatus();

        // schedule the task runner status to update every 5 seconds
        setInterval(getTaskRunnerStatus, 5000);
    });

</script>

<h2>AMRS Reports Settings</h2>

<b class="boxHeader">Required Metadata</b>
<div class="box" style="width:99%; height:auto; overflow-x:auto;">

    <table cellpadding="4" cellspacing="0">
        <tr>
            <td>CCC Number Identifier Type</td>
            <td id="cccIdentifierType">
                <form>
                    <input type="hidden" name="property" value="amrsreports.cccIdentifierType"/>
                    <select name="value" onchange="enableSaveFor('cccIdentifierType')">
                        <option value=""></option>
                        <c:forEach items="${identifierTypes}" var="identifierType">
                            <option value="${identifierType.id}"
                            <c:if test="${identifierType.id == cccIdentifierType}">selected</c:if>
                            >${identifierType.name}</option>
                        </c:forEach>
                    </select>
                    <input type="submit" name="save" onclick="return saveGPFor('cccIdentifierType')" value="save"/>
                    <span class="saved">saved</span>
                </form>
            </td>
        </tr>

        <tr>
            <td>TB Registration Attribute Type</td>
            <td id="tbRegistrationAttributeType">
                <form>
                    <input type="hidden" name="property" value="amrsreports.tbRegistrationAttributeType"/>
                    <select name="value" onchange="enableSaveFor('tbRegistrationAttributeType')">
                        <option value=""></option>
                        <c:forEach items="${attributeTypes}" var="attributeType">
                            <option value="${attributeType.id}"
                            <c:if test="${attributeType.id == tbRegistrationAttributeType}">selected</c:if>
                            >${attributeType.name}</option>
                        </c:forEach>
                    </select>
                    <input type="submit" name="save" onclick="return saveGPFor('tbRegistrationAttributeType')" value="save"/>
                    <span class="saved">saved</span>
                </form>
            </td>
        </tr>
        <tr>
            <td>Production Server URL</td>
            <td id="productionServerURL">
                <form>
                    <input type="hidden" name="property" value="amrsreports.productionServerURL"/>
                    <input type="text" name="value" onkeyup="enableSaveFor('productionServerURL')" value="${productionServerURL}" size="60"/>
                    <input type="submit" name="save" onclick="return saveGPFor('productionServerURL')" value="save"/>
                    <span class="saved">saved</span>
                </form>
            </td>
        </tr>
    </table>

</div>

<br />

<b class="boxHeader">Derived Table Updates</b>
<div class="box" style="width:99%; height:auto; overflow-x:auto; padding: 0.75em 0.5em;">

    <form>
        <div id="taskMessage">
            <span class="message"></span>
            <!--
            <button id="stopThread">Stop Running Task</button>
            -->
        </div>

        <div id="choices">
            <!-- // changing to use just a single task to build all three tables
            <div class="choice"><input type="radio" name="taskName" value="arvs"/> ARV Tables</div>
            <div class="choice"><input type="radio" name="taskName" value="pregnancy"/> Pregnancy Tables</div>
            -->
            <div class="choice"><input type="radio" name="taskName" value="enrollment"/> HIV Care Enrollment Table</div>
        </div>

        <div id="buttons">
            <button id="startTask">Start Task</button>
        </div>
    </form>

</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
