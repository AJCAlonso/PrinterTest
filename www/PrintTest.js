var exec = require('cordova/exec');

module.exports.add = function (arg0, success, error) {
    exec(success, error, 'PrintTest', 'add', [arg0]);
};

exports.nativeToast = function (arg0, success, error) {
    exec(success, error, 'PrintTest', 'nativeToast', [arg0]);
};