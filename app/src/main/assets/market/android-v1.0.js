/**
 * JS与客户端交互脚本 ANDROID
 * @author tianlupan 2015/10/23
 * @version 1.0
 */


jsapi.version={client:'android',version:'1.0'};

var appClient = window.appClient;

//请求app处理的任务数组,用于在结果返回时回调处理函数
var taskArray = new Array();

var listenerArray=new Array();

jsapi.callApp=function(taskType,taskArgs,onResult){

    //TODO 把listerner类型加里面
    if(taskType=="biz.navigation.setRight" ||  taskType=="biz.navigation.setLeft"){
        setListener(taskType,taskArgs,onResult);
        return;
    }

    callFromJs4Result(taskType,taskArgs,onResult);
};

function strToJson(str){
    var json = eval('(' + str + ')');
    return json;
}


function addTask(taskType, onResult) {
    var taskId=-1;
    if (onResult != null && onResult != undefined) {
       taskId= taskArray.push({taskType: taskType, onResult: onResult});
    }
    return taskId;
}

function wrapArgs(args) {
    if (args == undefined || args == null) {
        args = "";
    }

    if(typeof  args==="number"){
        args=String(args);
    }

    if(typeof  args==="object"){
        return JSON.stringify(args);
    }

    return args;

}


/**
 * 注意: JS调用 APP客户端并获取返回结果
 * @param taskType 任务类型，String类型，如getLocation
 * @param args 任务参数,如果为空设为null,可支持的类型String,Number,Arrays
 * @param onResult 结果返回处理函数 function(result);
 */
function callFromJs4Result(taskType, args, onResult) {
     var taskId= addTask(taskType, onResult);
    //检查参数
    args=wrapArgs(args);
    console.info("callFromJs, taskId="+taskId+",  taskType="+taskType+",args="+args);
    //调用客户端
    appClient.onCallFromJS(taskId ,taskType, args);
}


/**
 * JS主动向App发起请求后，通过此函数接收APP返回结果
 * 并调用通过 callFromJs4Result 设置的相应 结果处理函数 function(result);
 * APP端调用函数
 */
function onAppClientResult(jsonStr) {
    var json=strToJson(jsonStr);
    console.info("js 收到结果="+strToJson(jsonStr));
    var taskId=json.taskId;
    var result=json.result;
    if(taskId>0 && taskId<=taskArray.length){
        taskArray[taskId-1].onResult(json);
        taskArray[taskId]=null;
    }
}

//APP端调用函数
function onJSListener(jsonStr){
    var json=strToJson(jsonStr);
    var type=json.type;
    var index=getListenerIndex(type);
    var clickArg=json.clickArg;
    console.info("ONJSLISTENER, type="+type+",index="+index);
    if(index>=0){
        listenerArray[index].listener(clickArg);
    }
}

//APP端调用函数
function onJSPageResume(){
        console.log("新建页面返回");
}

Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};

Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

function getListenerIndex(type){
    for(var i=0;i<listenerArray.length;i++){
        if(listenerArray[i].type==type){
            return i;
        }
    }
    return -1;
}


function setListener(type,arg,listener){

    if(type=="" || arg=="") {
        console.info("setListener参数不正确");
        return;
    }

    console.info("setListener, type="+type+",arg="+arg);
    var json=strToJson(arg);
    if(json.text==""){
        clearListener(type);
    }else {
        var index =getListenerIndex(type);
        if (index < 0) {
            listenerArray.push({type: type, listener: listener});
        } else {
            listenerArray[index] = {type: type, listener: listener};
        }
    }
    appClient.onSetListenerFromJS(type,arg);
}

function clearListener(type){
    listenerArray.remove(type);
}

//taskId目前版本中是被忽略的，但taskType和args需要返回
jsapi.onCallFromApp= function (taskId,taskType, args){
    if(taskType=="biz.rpc.getUserInput"){
        appClient.onCallFromJS(taskId ,taskType, '{"status":1,"result":{"content":{"key":"value"}}}');
    }
}