/**
 * All of these methods come from the openmrs.js file in 1.9.x
 */

/**
 * Takes something like "param[start]" and returns "param\\[start\\]"
 * @param partialSelector the part of a jquery selector after the # or the .
 * @return the input, with the following characters escaped: #;&,.+*~':"!^$[]()=>|/@
 */
function escapeJquerySelector(partialSelector) {
    return partialSelector.replace(/#/g, '\\#').replace(/;/g, '\\;').replace(/&/g, '\\&').replace(/,/g, '\\,').replace(/\./g, '\\.').replace(/\+/g, '\\+').replace(/\*/g, '\\*').replace(/~/g, '\\~').replace(/'/g, "\\'").replace(/:/g, '\\:').replace(/"/g, '\\"').replace(/!/g, '\\!').replace(/\^/g, '\\^').replace(/\$/g, '\\$').replace(/\[/g, '\\[').replace(/\]/g, '\\]').replace(/\(/g, '\\(').replace(/\)/g, '\\)').replace(/=/g, '\\=').replace(/>/g, '\\>').replace('/\|/g', '\\|').replace(/\//, '\\/').replace(/@/g, '\\@');
}

/**
 * DateTimePicker class
 * @param dateFormat :String date format to use (ex: dd-mm-yyyy)
 * @param timeFormat :String time format to use (ex: hh:mm )
 * @param id :Element the html element (when id is not present)
 *           :String the id of the text box to use as the datetime picker
 * @param opts :Map additional options for the jquery datetime picker widget (included are ampm, separator, gotoCurrent)
 */
function DateTimePicker(dateFormat, timeFormat, id, opts) {
    var jq;
    if(typeof id == 'string') {
        id = escapeJquerySelector(id);
        jq = jQuery('#' + id);
    }
    else {
        jq = jQuery(id);
    }

    if(opts == null) {
        opts = {};
    }
    setOptions(opts, 'dateFormat', dateFormat.replace("yyyy", "yy"));//have to do the replace here because the datetimepicker only required 'yy' for 4-number year
    setOptions(opts, 'timeFormat', timeFormat);
    setOptions(opts, 'separator', " ");
    if( timeFormat.search(/t/i) != -1){
        setOptions(opts, 'ampm', true);
    }
    setOptions(opts, 'appendText', "(" + dateFormat+opts.separator+timeFormat+ ")");
    setOptions(opts, 'gotoCurrent', true);
    setOptions(opts, 'changeMonth', true);
    setOptions(opts, 'changeYear', true);
    setOptions(opts, 'showOtherMonths', true);
    setOptions(opts, 'selectOtherMonths', true);

    jq.datetimepicker(opts);

    this.show = function() {
        jq.datetimepicker("show");
    }
}

/**
 * TimePicker class
 * @param timeFormat :String time format to use (ex: hh:mm )
 * @param id :Element the html element (when id is not present)
 *           :String the id of the text box to use as the time picker
 * @param opts :Map additional options for the jquery datetime picker widget (included are ampm,separator, gotoCurrent)
 */
function TimePicker(timeFormat, id, opts) {
    var jq;
    if(typeof id == 'string') {
        id = escapeJquerySelector(id);
        jq = jQuery('#' + id);
    }
    else {
        jq = jQuery(id);
    }

    if(opts == null) {
        opts = {};
    }
    setOptions(opts, 'timeFormat', timeFormat);
    if( timeFormat.search(/t/i) != -1){
        setOptions(opts, 'ampm', true);
    }
    setOptions(opts, 'appendText', "(" +timeFormat+ ")");
    setOptions(opts, 'gotoCurrent', true);
    setOptions(opts, 'changeMonth', true);
    setOptions(opts, 'changeYear', true);
    setOptions(opts, 'showOtherMonths', true);
    setOptions(opts, 'selectOtherMonths', true);

    jq.timepicker(opts);

    this.show = function() {
        jq.timepicker("show");
    }
}

/**
 * Simple utility method to set a map value if the key doesnt exist
 * @param opts :Map
 * @param name :Object the key
 * @param value :Object the value (can also be a function)
 */
function setOptions(opts, name, value) {
    if(opts[name]) return;
    opts[name] = value;
}
