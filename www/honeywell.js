var execute = require("cordova/exec");

const PLUGIN_NAME = 'HoneywellScannerPlugin';

var honeywell = {
    softwareTriggerStart: function () {
        return execute(null, null, PLUGIN_NAME, 'softwareTriggerStart', []);
    },
    softwareTriggerStop: function () {
        return execute(null, null, PLUGIN_NAME, 'softwareTriggerStop', []);
    },
    listen: function (res, err) {
        return execute(res, err, PLUGIN_NAME, 'listen', []);
    },
    release: function () {
        return execute(null, null, PLUGIN_NAME, 'release', []);
    },
    claim: function () {
        return execute(null, null, PLUGIN_NAME, 'claim', []);
    },
    test: function (args, success, err) {
        console.log(success, err, args);
        return execute(success, err, PLUGIN_NAME, 'test', [args]);
    }
};

module.exports = honeywell;
