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

<openmrs:htmlInclude file="/moduleResources/amrsreports/css/amrsreports.css" />
<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

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

		$j('#xlsdownload').click(function() {
			window.open("downloadxls.htm?reportId=${report.id}", 'Download Excel File');
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

</script>

<%@ include file="localHeader.jsp"%>

<b class="boxHeader">View Report</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">

<c:if test="${not empty records}">
    <div id="printbuttons" align="right">
        <input type="button" id="xlsdownload" value="Download Excel Format">
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
                    <td><img src="${pageContext.request.contextPath}/moduleResources/amrsreports/images/format-indent-more.png" id="img" /></td>
                    <c:forEach var="rec" items="${record}">
                        <td>${rec}</td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<div id="dlgData" title="Patients More Information"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
