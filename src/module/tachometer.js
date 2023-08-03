

class Tachometer {
    draw(context, data) {
        var rpm = data["RPM"]
        var currRPM = rpm[0];
        var maxRPM = rpm[1] + 50;

        context.lineWidth = 2;
        context.save();
        context.translate(300, 300);
        context.rotate((currRPM / maxRPM) * 3 + Math.PI)
        context.fillRect(0, 0, 150, 10);
        context.restore();

// RPM Numbers
        for (var i = 0; i < Math.ceil(maxRPM / 100); i++) {
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
}

modules.push(new Tachometer())
console.log("hello")