function toUppercase(evt) {
    var comp = evt.getSource();
    comp.setValue(comp.getSubmittedValue().toUpperCase());
}

function removeEmptyOption(combiID) {
    return function (evt) {
        removeEmptyOptionClient(combiID);
        evt.cancel();
    }
}

function removeEmptyOptionList(combiID) {
    return function (evt) {
        combiID.forEach(function (entry) {
            removeEmptyOptionClient(entry);
        });
        evt.cancel();
    }
}

function removeEmptyOptionClient(combiID) {
    var component = AdfPage.PAGE.findComponentByAbsoluteId(combiID);
    if (component!=null && typeof document.forms[0].elements[combiID] != "undefined" && document.forms[0].elements[combiID].options[0].value == '') {
        $(document.forms[0].elements[combiID].options[0]).remove();
        component.setValue(0);
    }
}

function initOptionsFromServer(combosID) {
    combosID.forEach(function (entry) {
        removeEmptyOptionClient(entry);
    });
}

function onBtClickSimulation(buttonId) {
    return function (evt) {
        var val = evt.getSource().getSubmittedValue();

        //if(val!=null && val!=''){
        callBtnClick(buttonId);
        //}
    }
}

function callBtnClick(buttonId) {
    var button = AdfPage.PAGE.findComponentByAbsoluteId(buttonId);
    var isOk = button != null && button.getProperty("visible") == true && button.getProperty("disabled") == false;
    if (isOk)
        AdfActionEvent.queue(button, button.getPartialSubmit());
}

function handleBlur(MyCustomServerEvent, params, callback, calbbackParam) {
    return function (evt) {
        var inputTextComponent = evt.getSource();

        AdfCustomEvent.queue(inputTextComponent, MyCustomServerEvent, params, true);
        evt.cancel();

        if (typeof callback === "function")
            callback(calbbackParam);
    }
}

function setFocus(clientId, nextFocus) {
    var currentInput = $(":input[name='" + clientId + "']");
    var currentInputIndex = $(':input').index(currentInput);
    if (nextFocus) {
        var nextIndex = currentInputIndex + 1;
        var n = $(':input').length;

        if (nextIndex < n) {
            $(':input')[nextIndex].focus();
        }
        else 
        currentInput.focus();
    }
    else 
        currentInput.focus();
}

function trim() {
    return function (evt) {
        var textField = evt.getSource();
        $("input[name='" + textField.getClientId() + "']").val($("input[name='" + textField.getClientId() + "']").val().replace(/^\s+|\s+$/gm, ''))
    }
}

function showHidePopup(popupId, show) {
    return function (evt) {
        if (document.querySelector("div[data-afr-popupid]")){
            var popId = document.querySelector("div[data-afr-popupid]").getAttribute("data-afr-popupid");
            var popup = AdfPage.PAGE.findComponent(popId);
    
            if (show != null && show && popup != null)
                popup.show();
            else if (popup != null)
                popup.hide();
        }
    }
}

function askBeforeGoingOn(question) {
    return function (evt) {
        if (confirm(question))
            return true;
        else {
            evt.cancel();
            return false;
        }
    }
}

function closeReferto(Algoritmo, Dtspedizione, Idallegato) {
    return function (evt) {
        if (Algoritmo == null) {
            if (!askBeforeGoingOn('Non è stato trovato alcun algoritmo per il calcolo del gruppo di appartenenza in base al questionario adottato. Chiudere ugualmente il referto?')){
                evt.cancel();
                return false;
            }
        }

        beforeClosingReferto(Dtspedizione, Idallegato, evt);
    }
}

function beforeClosingReferto(dtspedizione,idallegato, evt){
    var reflettera = 'lascia';
    //se non esiste un allegato non succede nulla di strano
    if(idallegato=='' || idallegato==null){
       reflettera = 'crea';
    } else {
        if(dtspedizione=='' || dtspedizione==null)
            reflettera = 'ricrea';
        else {
            //se la lettera risulta già spedita devo chiedere all'utente cosa fare
            if(confirm('La lettera del referto in uso risulta gia\' stampata. Ricreare ugualmente la lettera?\n OK=ricrea la lettera\nAnnulla=aggiorna solo il database'))
                reflettera = 'ricrea';
            else
                reflettera = 'lascia';
        }
    }
    AdfCustomEvent.queue(evt.getSource(), "onChiudi", {ref_lettera:reflettera}, true);
    evt.cancel();
    return false;
}

function customHandler(buttonId) {
    var button = AdfPage.PAGE.findComponentByAbsoluteId(buttonId);
    var isOk = button != null && button.getProperty("disabled") == false;
    if (isOk){
        var actionEvent = new AdfActionEvent(button);
        actionEvent.forceFullSubmit();
        actionEvent.noResponseExpected();
        actionEvent.queue();
    }
}


/** disabilita il tasto destro del mouse**/
function right(e)
{

  if ((navigator.appName == 'Netscape' && (e.which == 3 || e.which == 2)) ||
      (navigator.appName == 'Microsoft Internet Explorer' && (event.button == 2 || event.button == 3)))
  {
    alert("Tasto destro non gestito");
    return false;
  }

  return true;
}
 
document.onmousedown=right;  // Capture in Netscape 6 e IE
if (document.layers)  // Capture in Nescape 4.x
{
  window.captureEvents(Event.MOUSEDOWN);
  window.onmousedown=right;
}

/** disabilita tasto backspace**/

function mykeyhandler(e)
{

    var ev = e ? e : window.event;
     var el = ev.target ? ev.target : ev.srcElement;   
    if (ev && ev.keyCode == 8 && el.tagName != "INPUT" && el.tagName != "TEXTAREA")
    {
      ev.cancelBubble = true;
      ev.returnValue = false;
      return false;
    }

}

document.onkeydown = mykeyhandler; // Capture in Netscape 6 e IE
if (document.layers)  // Capture in Nescape 4.x
{
  window.captureEvents(Event.KEYDOWN);
  window.onmousedown=mykeyhandler;
}

/** disabilita la visualizzazione del link sulla riga di stato*/
document.onmouseover = function ( e )
{   
  if ( !e )
    e = window.event;   
  var el = e.target ? e.target : e.srcElement;   
  while ( el != null && el.tagName != "A" )
    el = el.parentNode;   
  if ( el == null )
    return;   
  if ( e.preventDefault )
    e.preventDefault();   
  else
  e.returnValue = true;
};

