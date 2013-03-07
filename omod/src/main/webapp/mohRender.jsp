<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreport/mohRender.form" />

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.tools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/js/ZeroClipboard.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/css/smoothness/jquery-ui-1.8.16.custom.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/css/TableTools_JUI.css" />

<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

<style type="text/css">
	.tblformat tr:nth-child(odd) {
		background-color: #009d8e;
		color: #FFFFFF;
	}
	.tblformat tr:nth-child(even) {
		background-color: #d3d3d3;
		color: #000000;
	}
    .visualPadding { margin: 1em; }
/*
    .oneThird { float: left; width: 30%; min-height: 10em; }
*/
    .newline { clear: both; display: block; }
</style>

<script type="text/javascript">

	$j(document).ready(function(){

		var ty = $j('#tblMain').dataTable({
			"bJQueryUI":false,
			"sPaginationType": "full_numbers",
			"sDom": 'T<"clear">lfrtip',
			"oTableTools": {
				"sRowSelect": "single",
				"aButtons": [
					"print"
				]
			}
		} );

		$j('#tblMain').delegate('tbody td #imgrender','click', function() {
			var trow=this.parentNode.parentNode;
			var aData21 = ty.fnGetData(trow);
			var amrsid1=aData21[1].trim();
			DWRAmrsReportService.viewMoreDetailsRender("${fileToManipulate}",amrsid1,processThis);

		});

		$j("#dlgData" ).dialog({
			autoOpen:false,
			modal: true,
			show: 'slide',
			height: 'auto',
			hide: 'slide',
			width:600,
			cache: false,
			position: 'top',
			buttons: {
				"Exit": function () { $j(this).dialog("close"); }
			}
		});

		function processThis(data){
			$j("#dlgData").empty();
			var listSplit=data.split(",");

			maketable(listSplit);

			$j("#dlgData").dialog("open");
		}

		$j('#csvdownload').click(function() {
			window.open("downloadcsvR.htm?fileToImportToCSV=${fileToManipulate}", 'Download csv');
			return false;
		});

        // trim all date values to 10 characters .. ugh.
        $j("input[name=reportDate]").each(function(){
            $j(this).val($j(this).val().substring(0, 10));
        });

    });

	function clearDataTable(){
		//alert("on change has to take effect");
		var hidepic= document.getElementById("maindetails");
		var titleheader=document.getElementById("titleheader");
		hidepic.style.display='none';
		titleheader.style.display='none';
	}

	function maketable(info1){
		row=new Array();
		cell=new Array();

		row_num=info1.length; //edit this value to suit

		tab=document.createElement('table');
		tab.setAttribute('id','tblSummary');
		tab.setAttribute('border','0');
		tab.setAttribute('cellspacing','2');
		tab.setAttribute('class','tblformat');

		tbo=document.createElement('tbody');

		for(c=0;c<row_num;c++){
			var rowElement=info1[c].split(":");
			row[c]=document.createElement('tr');

			for(k=0;k<rowElement.length;k++) {
				cell[k]=document.createElement('td');
				cont=document.createTextNode(rowElement[k])
				cell[k].appendChild(cont);
				row[c].appendChild(cell[k]);
			}
			tbo.appendChild(row[c]);
		}
		tab.appendChild(tbo);
		document.getElementById('dlgData').appendChild(tab);
	}
</script>

<c:if test="${not empty loci}">
	<div id="titleheader">
		<table align="right">
			<tr>
				<td><b>History Report for:</b></td>
				<td><u>${loci}</u></td>
				<td><b>As at:</b></td>
				<td><u>${time}</u></td>
			</tr>
		</table>
	</div>
</c:if>

<%@ include file="localHeader.jsp"%>

<b class="boxHeader">Run AMRS Reports</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST" name="amrsreportrenderer" action="mohRender.form">
        <fieldset class="visualPadding oneThird">
            <legend>Report Date (as of)</legend>
            <c:forEach items="${reportDates}" var="reportDate">
                <input type="radio" name="reportDate" value='<openmrs:formatDate date="${reportDate}" type="textbox"/>'>
                <openmrs:formatDate date="${reportDate}" type="textbox"/> <br/>
            </c:forEach>
        </fieldset>
        <fieldset class="visualPadding oneThird">
            <legend>Location</legend>
            <c:forEach var="location" items="${locations}">
                <input type="radio" name="location" value="${location.locationId}"/> ${location.name} <br/>
            </c:forEach>
        </fieldset>
        <fieldset class="visualPadding oneThird">
            <legend>Reports</legend>
            <input type="radio" name="hardcoded" value="ack"/> MOH 361A <br/>
        </fieldset>
        <input id="submitButton" class="visualPadding newline" type="submit" value="View"/>
    </form>
</div>

<c:if test="${not empty records}">
	<b class="boxHeader">Report Details</b>
	<div class="box" id="maindetails" style=" width:99%; height:auto;  overflow-x: auto;">
		<div id="printbuttons" align="right">
			<input type="button" id="csvdownload" value="Download CSV Format">
		</div>

		<table border="0" id="tblMain">
			<thead>
				<tr>
					<th>View</th>
					<c:forEach var="col" items="${columnHeaders}">
						<th>${col}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="row" items="${records}">
					<tr>
						<td><img src="${pageContext.request.contextPath}/moduleResources/amrsreport/images/format-indent-more.png" id="imgrender" /></td>
							<c:forEach var="cell" items="${row}">
							<td>${cell}</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>

	</div>
</c:if>

	<div id="dlgData" title="Patients More Information"></div>

	<input type="hidden" value="${fileToManipulate}" name="fileToImportToCSV">

<%@ include file="/WEB-INF/template/footer.jsp"%>
