window.onload = function () {
    console.log("loaded")
    loop()
}

var modules = []

async function loop() {
    while (true) {
        var data
        try {
            await getData().then((result) => {
                data = JSON.parse(result)
            })
            document.getElementById("rpmNumber").innerHTML = data["RPM"];
            rpmMeter(data["RPM"])
            checkJunk(data["ZeroJunk"], data["NonZeroJunk"], data["RPM"])
            lapInfo(data["Lap Info"])
            draw(data)
            await sleep(16)
        } catch (ignored) {
            console.log("Waiting for data")
            await sleep(5000)
        }
    }
}

function lapInfo(data) {
    document.getElementById("lapInfo").innerHTML = data.join(", ")
}

function checkJunk(zeroJunk, nonZeroJunk, valid) {
    for (var i = 0; i < zeroJunk.length; i++) {
        var zeroMarker = document.getElementById("zeroChangeMarker").innerHTML
        if (zeroJunk[i] != 0 && zeroMarker === "F" && i != 8 && i != 9 && i != 10 && i != 15) {
            document.getElementById("zeroChangeMarker").innerHTML = "T " + i + " " + zeroJunk[i]
        }
    }
    for (var i = 0; i < nonZeroJunk.length; i++) {

        var nonZeroMarker = document.getElementById("nonZeroChangeMarker").innerHTML
        if (nonZeroJunk[i] != 1 && nonZeroMarker === "F" && valid[1] != 0) {
            document.getElementById("nonZeroChangeMarker").innerHTML = "T " + i + " " + nonZeroJunk[i]

        }

    }
    var str = ""
    for (var i = 0; i < zeroJunk.length; i++) {
        str += "(" + i + " " + zeroJunk[i] + "), "
    }
    document.getElementById("splits").innerHTML = str
}

function rpmMeter(data) {
    var max = data[1]
    var curr = data[0]
    var maxStr = "";
    for (var i = 0; i <= Math.ceil(max / 100); i++) {
        maxStr += i + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
    }
    var lowStr = ""
    for (var i = 0; i <= (Math.ceil(max / 100)) / 2; i++) {
        if (curr > i * 100) {
            lowStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    var medStr = ""
    for (var i = (Math.ceil(max / 100)) / 2; i <= (Math.ceil(max / 100)) / 1.33; i++) {
        if (curr > i * 100) {
            medStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    var highStr = ""
    for (var i = (Math.ceil(max / 100)) / 1.33; i <= (Math.ceil(max / 100)); i++) {
        if (curr > i * 100) {
            highStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    document.getElementById("rpmMax").innerHTML = maxStr
    document.getElementById("rpmLow").innerHTML = lowStr
    document.getElementById("rpmMid").innerHTML = medStr
    document.getElementById("rpmHigh").innerHTML = highStr
}

async function getData() {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    var res;

    await fetch("http://127.0.0.1:8000/telemetry", requestOptions)
        .then(response => response.text())
        .then(result => res = result)
        .catch(error => console.log('error', error));

    return JSON.parse(res);
}

async function getRPM() {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    var rpm = NaN;

    await fetch("http://127.0.0.1:8000/rpm", requestOptions)
        .then(response => response.text())
        .then(result => rpm = result)
        .catch(error => console.log('error', error));

    return rpm.split("\n");
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function draw(data) {
    var rpm = data["RPM"]
    var canvas = document.getElementById("canvas");
    canvas.width = 600;
    canvas.height = 600;
    var context = canvas.getContext("2d");
    context.scale(1, 1);
    var currRPM = rpm[0];
    var maxRPM = rpm[1] + 50;

    // RPM Needle
    context.lineWidth = 2;
    context.save();
    context.translate(300, 300);
    context.rotate((currRPM / maxRPM) * 3 + Math.PI)
    context.fillRect(0, 0, 150, 10);
    context.restore();

    // RPM Numbers
    for(var i = 0; i < Math.ceil(maxRPM / 100); i++) {
        var x = Math.cos(Math.PI + (i / Math.ceil(maxRPM / 100)) * Math.PI) * 180 + 300;
        var y = Math.sin(Math.PI + (i / Math.ceil(maxRPM / 100)) * Math.PI) * 180 + 300;
        context.save();
        context.font = "24px Times New Roman"
        context.fillText(i + "", x, y)
        context.restore()
    }
    context.save()
    context.fillStyle = "rgb(255, 0, 0)"
    context.font = "32px Times New Roman"
    context.fillText(Math.ceil(maxRPM / 100), 480, 300)
    context.restore()
}

async function add() {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    var res

    var query = document.getElementById("mods").value
    console.log(query)
    await fetch("http://127.0.0.1:8000/module?q=" + query, requestOptions)
        .then(response => response.text())
        .then(result => res = result)
        .catch(error => console.log('error', error));

    console.log(res)
}