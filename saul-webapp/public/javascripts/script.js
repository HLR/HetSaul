$(document).ready(function(){

        $("#fileList").children("li").each(function(){
            installTabClickedAction($(this));
        })


        $("#compileBtn").click(function(){
            updateCode();
        });

        $("#plusFile").click(function(){
            newFile(); 
        });
})

var newFile = function(){
    var li = $("<li class='active'><a href='#'></a></li>");
    var idx = $("#fileList").children("li").size() + 1;
    li.children().each(function(){
        $(this).text('test'+idx+'.scala');
    });
    $("#fileList").children(".active").removeClass("active"); 
    li.attr('id','fileName' + idx);
    $("#fileList").append(li);
    installTabClickedAction(li);
    var code = $("<textarea class='form-control code active' rows='18'>");
    code.attr('id','code' + idx);
    $("#workspace").children(".active").each(function(){
        $(this).hide();
        $(this).removeClass("active");
    })
    $("#workspace").append(code);
}

var installTabClickedAction = function(tab){
    tab.click(function(){
        $("#fileList").children(".active").each(function(){
            $(this).removeClass("active");
            
        })

        tab.addClass("active");

        var codeId = "#code" + tab.attr('id').slice(-1);
        $("#workspace").children(".active").each(function(){
            $(this).removeClass("active");
            $(this).hide();
        })
        $(codeId).addClass("active");
        $(codeId).show();
    })
}

var getAllFiles = function(){
    var files = {}
    $("#fileList").children("li").each(function(index){
        var idx = $(this).attr('id').slice(-1);
        var codeId = "#code" + idx;
        var codeName = $(this).text();
        files[codeName] = $(codeId).val();
    })

    return JSON.stringify(files);
    
}
var updateCode = function(){
    var callback = {
        success : onSuccess,
        error : onError
    }
    
    //jsRoutes.controllers.Application.updateCode($("#code1").text()).ajax(callback);
    $.ajax({
        type : 'POST',
        url : '/updateCode',
        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
                },
        data : getAllFiles(),
        success : onSuccess,
        error: onError
        });
};

var onSuccess = function(data){
    alert(data);
}
var onError = function(data){
    alert(data);
}
