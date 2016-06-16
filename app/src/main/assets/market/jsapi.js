var jsapi = {
    version: {client: '', version: ''},
    init: function () {
    },
    //taskArgs及下面的args允许的数据类型为：json | String | null
    //如果app当前版本不支持taskType,统一返回:UNKNOWN_TASK
    callApp: function (taskType, taskArgs, onResult) {
    },
    //APP主动发起的请求
    onCallFromApp: function (taskId,taskType, args) {
    }
};






