var execute = require("cordova/exec");

const PLUGIN_NAME = 'HoneywellScannerPlugin';

var honeywell = {
    claim: function (success, err) {
        return execute(success, err, PLUGIN_NAME, 'claim', []);
    },
    release: function (success, err) {
        return execute(success, err, PLUGIN_NAME, 'release', []);
    }
};

module.exports = honeywell;
