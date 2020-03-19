var exec = require('cordova/exec');

module.exports.add = function (arg0, success, error) {
    exec(success, error, 'PrintTest', 'add', [arg0]);
};

exports.nativeToast = function (arg0, success, error) {
    exec(success, error, 'PrintTest', 'nativeToast', [arg0]);
};

exports.Print_QRCode = function (arg0, success, error) {
    exec(success, error, 'PrintTest', 'Print_QRCode', [arg0]);
};

exports.Print_Ticket = function (arg0, success, error) {
    exec(success, error, 'PrintTest', 'Print_Ticket', [arg0]);
};