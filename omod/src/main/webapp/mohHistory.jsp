<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/mohHistory.form" />

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/jquery.tools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/ZeroClipboard.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jspdf.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/smoothness/jquery-ui-1.8.16.custom.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools_JUI.css" />
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
</style>
<script type="text/javascript">
	$j(document).ready(function(){

		var ti = $j('#tablehistory').dataTable({
			"bJQueryUI":false,
			"sPaginationType": "full_numbers",
			"sDom": 'T<"clear">lfrtip',
			"oTableTools": {
				"sRowSelect": "single",
				"aButtons": [
					"print"
				]

			}
		});

		$j('#tablehistory').delegate('tbody td #img','click', function() {
			var trow=this.parentNode.parentNode;
			var aData2 = ti.fnGetData(trow);
			var amrsNumber=aData2[1].trim();
			DWRAmrsReportService.viewMoreDetails("${historyURL}", amrsNumber,callback);
			return false;
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

		function callback(data){
			$j("#dlgData").empty();
			//alert(data)

			var listSplit=data.split(",");

			maketable(listSplit);

			$j("#dlgData").dialog("open");
		}

		$j('#pdfdownload').click(function() {
			downLoadPDF();
		});
		$j('#csvdownload').click(function() {
			window.open("downloadcsv.htm?fileToImportToCSV=${historyToCSV}", 'Download csv');
			return false;
		});
	});

	function clearDataTable(){
		//alert("on change has to take effect");
		dwr.util.removeAllRows("tbodydata");
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
			//alert(rowElement.length) ;

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

	function downLoadPDF(){
		var pdfDocument = new jsPDF('landscape');
		pdfDocument.text(20, 20, 'Comprehensive Care Clinic');
		pdfDocument.text(20, 25, 'PRE-ART REGISTER');
		pdfDocument.text(20, 30, 'MOH 361 A');
		pdfDocument.output('datauri');
	}
</script>

<c:if test="${not empty loci}">
	<div id="titleheader">
		<table align="right" id="tocheckout">
			<thead></thead>
			<tbody>
				<tr id="tocheck">
					<td><b>History Report for:</b></td>
					<td><u>${loci}</u></td>
					<td><b>As at:</b></td>
					<td><u>${time}</u></td>
				</tr>
			</tbody>
		</table>
	</div>
</c:if>

<%@ include file="localHeader.jsp"%>

<c:if test="${not empty queuedReports}">

    <b class="boxHeader">Queued Reports</b>
    <div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
            <c:forEach var="r" items="${queuedReports}">
                ${r.reportName} for ${r.location} as of ${r.evaluationDate} (run on ${r.dateScheduled}) <br/>
            </c:forEach>
    </div>
    <br />
</c:if>

<b class="boxHeader">View AMRS Reports</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form action="mohHistory.form" method="POST">
        <label>Select file For location</label> <br/>
        <c:forEach var="rpthistory" items="${reportHistory}">
            <input type="radio" name="history" value="${rpthistory}"/> ${rpthistory} <br/>
        </c:forEach>
        <input type="submit" value="View" id="collectFile">
    </form>
</div>
<br />

<c:if test="${not empty records}">
	<b class="boxHeader">Report Details</b>
	<div class="box" id="maindetails" style="width:99%; height:auto;  overflow-x: auto;">
		<div id="printbuttons" align="right">
			<input type="button" id="csvdownload" value="Download CSV Format">
		</div>

		<table id="tablehistory">
			<thead>
				<tr>
					<th>View</th>
					<c:forEach var="col" items="${columnHeaders}">
						<th>${col}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody id="tbodydata">
				<c:forEach var="record" items="${records}">
					<tr>
						<td><img src="${pageContext.request.contextPath}/moduleResources/amrsreport/images/format-indent-more.png"  id="img" /></td>
						<c:forEach var="rec" items="${record}">
							<td>${rec}</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:if>

<div id="dlgData" title="Patients More Information"></div>

<input type="hidden" value="${historyToCSV}" name="fileToImportToCSV">

<%@ include file="/WEB-INF/template/footer.jsp"%>
