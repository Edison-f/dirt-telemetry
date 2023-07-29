window.onload = function () {
    console.log("loaded")
    loop()
}

async function loop() {

    while (true) {
        let data
        await getData().then((result) => {
            data = JSON.parse(result)
        })
        document.getElementById("rpmNumber").innerHTML = data["RPM"];
        rpmMeter(data["RPM"])
        checkJunk(data["ZeroJunk"], data["NonZeroJunk"], data["RPM"])
        await sleep(16)
    }
}

function checkJunk(zeroJunk, nonZeroJunk, valid) {
    for (let i = 0; i < zeroJunk.length; i++) {
        let zeroMarker = document.getElementById("zeroChangeMarker").innerHTML
        if (zeroJunk[i] != 0 && zeroMarker === "F") {
            document.getElementById("zeroChangeMarker").innerHTML = "T " + i + " " + zeroJunk[i]
        }
    }
    for (let i = 0; i < nonZeroJunk.length; i++) {

        let nonZeroMarker = document.getElementById("nonZeroChangeMarker").innerHTML
        if (nonZeroJunk[i] != 1 && nonZeroMarker === "F" && valid[1] != 0) {
            document.getElementById("nonZeroChangeMarker").innerHTML = "T " + i + " " + nonZeroJunk[i]

        }

    }
}

function rpmMeter(data) {
    let max = data[1]
    let curr = data[0]
    let maxStr = "";
    for (let i = 0; i <= Math.ceil(max / 100); i++) {
        maxStr += i + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
    }
    let lowStr = ""
    for (let i = 0; i <= (Math.ceil(max / 100)) / 2; i++) {
        if (curr > i * 100) {
            lowStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    let medStr = ""
    for (let i = (Math.ceil(max / 100)) / 2; i <= (Math.ceil(max / 100)) / 1.33; i++) {
        if (curr > i * 100) {
            medStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    let highStr = ""
    for (let i = (Math.ceil(max / 100)) / 1.33; i <= (Math.ceil(max / 100)); i++) {
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
    let myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    let requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    let res;

    await fetch("http://127.0.0.1:8000/telemetry", requestOptions)
        .then(response => response.text())
        .then(result => res = result)
        .catch(error => console.log('error', error));

    return JSON.parse(res);
}

async function getRPM() {
    let myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    let requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    let rpm = NaN;

    await fetch("http://127.0.0.1:8000/rpm", requestOptions)
        .then(response => response.text())
        .then(result => rpm = result)
        .catch(error => console.log('error', error));

    return rpm.split("\n");
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

