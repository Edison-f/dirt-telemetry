window.onload = function () {
    console.log("loaded")
    loop()
}


async function loop() {

    while (true) {
        let rpm = NaN;
        await getRPM().then((result) => {
            rpm = result
        })
        document.getElementById("rpmNumber").innerHTML = rpm
        rpmMeter(rpm)
        await sleep(16)
    }
}

function rpmMeter(data) {
    let max = data[2]
    let curr = data[1]
    let maxStr = "";
    for(let i = 0; i <= Math.ceil(max / 100); i++) {
        maxStr += i + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
    }
    let lowStr = ""
    for(let i = 0; i <= (Math.ceil(max / 100)) / 2; i++) {
        if(curr > i * 100) {
            lowStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    let medStr = ""
    for(let i = (Math.ceil(max / 100)) / 2; i <= (Math.ceil(max / 100)) / 1.33; i++) {
        if(curr > i * 100) {
            medStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    let highStr = ""
    for(let i = (Math.ceil(max / 100)) / 1.33; i <= (Math.ceil(max / 100)); i++) {
        if(curr > i * 100) {
            highStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    document.getElementById("rpmMax").innerHTML = maxStr
    document.getElementById("rpmLow").innerHTML = lowStr
    document.getElementById("rpmMid").innerHTML = medStr
    document.getElementById("rpmHigh").innerHTML = highStr
}

async function getRPM() {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var requestOptions = {
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

