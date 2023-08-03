// noinspection JSIgnoredPromiseFromCall

window.onload = function () {
    console.log("loaded")
    loop()
}

const modules = [];
const canvasList = [];

async function loop() {
    // noinspection InfiniteLoopJS
    while (true) {
        let data;
        try {
            await getData().then((result) => {
                data = JSON.parse(result)
            })
            document.getElementById("rpmNumber").innerHTML = data["RPM"];
            rpmMeter(data["RPM"])
            draw(data)
            await sleep(16)
        } catch (ignored) {
            console.log("Waiting for data")
            await sleep(5000)
        }
    }
}

function rpmMeter(data) {
    const max = data[1];
    const curr = data[0];
    let maxStr = "";
    for (let i = 0; i <= Math.ceil(max / 100); i++) {
        maxStr += i + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
    }
    let lowStr = "";
    for (let i = 0; i <= (Math.ceil(max / 100)) / 2; i++) {
        if (curr > i * 100) {
            lowStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    let medStr = "";
    for (let i = (Math.ceil(max / 100)) / 2; i <= (Math.ceil(max / 100)) / 1.33; i++) {
        if (curr > i * 100) {
            medStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
    }
    let highStr = "";
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
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    let res;

    await fetch(window.location.href.split("/view")[0] + "/telemetry", requestOptions)
        .then(response => response.text())
        .then(result => res = result)
        .catch(error => console.log('error', error));

    return JSON.parse(res);
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function draw(data) {
    for (let i = 0; i < modules.length; i++) {
        let canvas;
        if(i >= canvasList.length) {
            canvas = document.createElement("canvas")
            document.body.appendChild(canvas);
            canvasList.push(canvas)
        } else {
            canvas = canvasList[i]
        }
        const curr = modules[i];
        canvas.width = curr.WIDTH;
        canvas.height = curr.HEIGHT;
        const context = canvas.getContext("2d");
        context.scale(curr.X_SCALE, curr.Y_SCALE);
        curr.draw(context, data)
    }

}

async function add() {
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    const query = document.getElementById("mods").value;

    await fetch(window.location.href.split("/view")[0] + "/module?q=" + query, requestOptions)
        .then(response => response.text())
        .then(result => document.getElementById("body").innerHTML += result)
        .catch(error => console.log('error', error));

    for (let i = query.split(",").length; i >= 0; i--) {
        const scripts = document.querySelectorAll("script");
        (0, eval)(scripts[scripts.length - 1 - i].textContent);
    }

}