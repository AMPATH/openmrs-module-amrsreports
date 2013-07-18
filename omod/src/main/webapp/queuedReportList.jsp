<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/diQuery-collapsiblePanel.js"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/css/diQuery-collapsiblePanel.css" />

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.list" />

<script type="text/javascript">
    $j(document).ready(function(){

        $j('.show_hide').showHide({
            speed: 1000,  // speed you want the toggle to happen
           // easing: '',  // the animation effect you want. Remove this line if you dont want an effect and if you haven't included jQuery UI
            changeText: 1, // if you dont want the button text to change, set this to 0
            showText: 'View',// the button text to show when a div is closed
            hideText: 'Close' // the button text to show when a div is open

        });
    });
</script>
<style>
    .subheading { height: 3em; }
    .subheading th { font-size: 120%; font-weight: normal; text-align: left !important; }
    #reportTable th, #reportTable td { text-align: left; }
    .spancontent {float:right;color: green;font-weight: bold;}

</style>

<%@ include file="localHeader.jsp"%>

<a href="queuedReport.form">Add a Scheduled Report</a>

<br />
<br />

<b class="boxHeader">View AMRS Reports</b>
<div class="box">

    <br/>





    <table cellpadding="2" cellspacing="0" id="reportTable" width="98%">

        <c:if test="${not empty queuedReports}">
            <tr class="subheading">
                <th colspan="4">Queued Reports</th>
            </tr>
            <tr>
                <td colspan="4">
                    <c:forEach var="f" items="${queuedReports}" >

                        <table cellpadding="2" cellspacing="0"  width="100%">
                            <tr>
                                <td colspan="4"><HR size="2"></td>

                            </tr>
                            <tr>
                                <td colspan="4"><div align="left"><strong>"${f.key}"</strong><span class="spancontent"><c:if test="${fn:length(f.value) gt 2}"><a class="show_hide" href="#" rel="#q${f.key.code}">View More</a></c:if></span></div></td>

                            </tr>
                            <tr>
                                <td colspan="4"><HR size="2"></td>

                            </tr>



                            <c:forEach var="r" items="${f.value}" varStatus="status">
                                <c:choose>
                                    <c:when test="${status.index < 2 }">

                                        <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                            <td>
                                                <a href="viewReport.form?reportId=${r.id}">View</a>
                                                <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                            </td>
                                            <td >${r.reportName}</td>
                                            <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                            <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                        </tr>
                                        <c:if test="${status.last }">
                                            <tr><td colspan="4"><HR size="3" ></td></tr>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${status.index == 2 }">
                                            <tr><td colspan="4">&nbsp;</td></tr>

                                            <tr>
                                                <td colspan="4"><div id="q${f.key.code}" class="toggleDiv" style="display: none;">
                                                    <table width="100%">



                                        </c:if>
                                        <c:if test="${status.index >= 2 }">

                                            <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                                <td>
                                                    <a href="viewReport.form?reportId=${r.id}">View</a>
                                                    <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                                </td>
                                                <td >${r.reportName}</td>
                                                <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                                <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${status.index >= 2 and status.last }">
                                                        </table>
                                                      </div>
                                                    </td>
                                                </tr>
                                                <tr><td colspan="4"><HR size="3" ></td></tr>
                                        </c:if>

                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                        </table>
                    </c:forEach>
                </td>
            </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
    </c:if>



<c:if test="${not empty runningReports}">
    <tr class="subheading">
        <th colspan="4">Running Reports</th>
    </tr>
    <tr>
        <td colspan="4">
            <c:forEach var="f" items="${runningReports}" >

                <table cellpadding="2" cellspacing="0"  width="100%">
                    <tr>
                        <td colspan="4"><HR size="2"></td>

                    </tr>
                    <tr>
                        <td colspan="4"><div align="left"><strong>"${f.key}"</strong><span class="spancontent"><c:if test="${fn:length(f.value) gt 2}"><a class="show_hide" href="#" rel="#r${f.key.code}">View More</a></c:if></span></div></td>

                    </tr>
                    <tr>
                        <td colspan="4"><HR size="2"></td>

                    </tr>


                    <c:forEach var="r" items="${f.value}" varStatus="status">
                        <c:choose>
                            <c:when test="${status.index < 2 }">

                                <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                    <td>
                                        <a href="viewReport.form?reportId=${r.id}">View</a>
                                        <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                    </td>
                                    <td >${r.reportName}</td>
                                    <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                    <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                </tr>
                                <c:if test="${status.last }">
                                    <tr><td colspan="4"><HR size="3" ></td></tr>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${status.index == 2 }">
                                    <tr><td colspan="4">&nbsp;</td></tr>

                                    <tr>
                                        <td colspan="4"><div id="r${f.key.code}" class="toggleDiv" style="display: none;">
                                            <table width="100%">



                                </c:if>
                                <c:if test="${status.index >= 2 }">

                                    <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                        <td>
                                            <a href="viewReport.form?reportId=${r.id}">View</a>
                                            <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                        </td>
                                        <td >${r.reportName}</td>
                                        <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                        <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                    </tr>
                                </c:if>
                                <c:if test="${status.index >= 2 and status.last }">
                                            </table>
                                          </div>
                                        </td>
                                       </tr>
                                       <tr><td colspan="4"><HR size="3" ></td></tr>
                                </c:if>

                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                </table>
            </c:forEach>
        </td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>
</c:if>




<c:if test="${not empty completeReports}">
            <tr class="subheading">
                <th colspan="4">Completed Reports</th>
            </tr>
           <tr>
               <td colspan="4">
                   <c:forEach var="f" items="${completeReports}" >

                       <table cellpadding="2" cellspacing="0"  width="100%">
                           <tr>
                               <td colspan="4"><HR size="2"></td>

                           </tr>
                           <tr>
                               <td colspan="4"><div align="left"><strong>"${f.key}"</strong><span class="spancontent"><c:if test="${fn:length(f.value) gt 2}"><a class="show_hide" href="#" rel="#c${f.key.code}">View More</a></c:if></span></div></td>

                           </tr>
                           <tr>
                               <td colspan="4"><HR size="2"></td>

                           </tr>


                           <c:forEach var="r" items="${f.value}" varStatus="status">
                               <c:choose>
                                   <c:when test="${status.index < 2 }">

                                       <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                           <td>
                                               <a href="viewReport.form?reportId=${r.id}">View</a>
                                               <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                           </td>
                                           <td >${r.reportName}</td>
                                           <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                           <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                       </tr>
                                       <c:if test="${status.last }">
                                           <tr><td colspan="4"><HR size="3" ></td></tr>
                                       </c:if>
                                   </c:when>
                                   <c:otherwise>
                                       <c:if test="${status.index == 2 }">
                                           <tr><td colspan="4">&nbsp;</td></tr>

                                           <tr>
                                               <td colspan="4"><div id="c${f.key.code}" class="toggleDiv" style="display: none;">
                                                   <table width="100%">



                                       </c:if>
                                       <c:if test="${status.index >= 2 }">

                                           <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                               <td>
                                                   <a href="viewReport.form?reportId=${r.id}">View</a>
                                                   <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                               </td>
                                               <td >${r.reportName}</td>
                                               <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                               <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                           </tr>
                                       </c:if>
                                       <c:if test="${status.index >= 2 and status.last }">
                                           </table>
                                            </div>
                                             </td>
                                               </tr>
                                               <tr><td colspan="4"><HR size="3" ></td></tr>
                                       </c:if>

                                   </c:otherwise>
                               </c:choose>
                           </c:forEach>

                       </table>
                   </c:forEach>
               </td>
           </tr>
           <tr><td colspan="4">&nbsp;</td></tr>
</c:if>

<c:if test="${not empty errorReports}">
    <tr class="subheading">
        <th colspan="4">Error Reports</th>
    </tr>
    <tr>
        <td colspan="4">
            <c:forEach var="f" items="${errorReports}" >

                <table cellpadding="2" cellspacing="0"  width="100%">
                    <tr>
                        <td colspan="4"><HR size="2"></td>

                    </tr>
                    <tr>
                        <td colspan="4"><div align="left"><strong>"${f.key}"</strong><span class="spancontent"><c:if test="${fn:length(f.value) gt 2}"><a class="show_hide" href="#" rel="#e${f.key.code}">View More</a></c:if></span></div></td>

                    </tr>
                    <tr>
                        <td colspan="4"><HR size="2"></td>

                    </tr>


                    <c:forEach var="r" items="${f.value}" varStatus="status">
                        <c:choose>
                            <c:when test="${status.index < 2 }">

                                <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                    <td>
                                        <a href="viewReport.form?reportId=${r.id}">View</a>
                                        <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                    </td>
                                    <td >${r.reportName}</td>
                                    <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                    <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                </tr>
                                <c:if test="${status.last }">
                                    <tr><td colspan="4"><HR size="3" ></td></tr>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${status.index == 2 }">
                                    <tr><td colspan="4">&nbsp;</td></tr>

                                    <tr>
                                        <td colspan="4"><div id="e${f.key.code}" class="toggleDiv" style="display: none;">
                                            <table  width="100%">



                                </c:if>
                                <c:if test="${status.index >= 2 }">

                                    <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                                        <td>
                                            <a href="viewReport.form?reportId=${r.id}">View</a>
                                            <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                        </td>
                                        <td >${r.reportName}</td>
                                        <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                                        <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                                    </tr>
                                </c:if>
                                <c:if test="${status.index >= 2 and status.last }">
                                           </table>
                                        </div>
                                      </td>
                                   </tr>
                                   <tr><td colspan="4"><HR size="3" ></td></tr>
                                </c:if>

                                </c:otherwise>
                        </c:choose>
                    </c:forEach>

                </table>
            </c:forEach>
        </td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>
</c:if>

</table>

<br/>

</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
