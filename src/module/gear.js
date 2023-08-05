class Gear {
    WIDTH = 50;
    HEIGHT = 100;
    X_SCALE = 1.0;
    Y_SCALE = 1.0;
    draw(context, data) {
        const currGear = data["Gear"][0]
        const maxGear = data["Gear"][1]

        context.save();
        context.strokeStyle = "rgb(0, 0, 0)"
        context.translate(0, 0);
        context.lineWidth = 1;
        context.strokeRect(0, 0, 50, 100);
        context.restore();

        context.save()
        context.translate(0, this.HEIGHT - 50)
        context.fillStyle = "rgb(0, 0, 255)";
        context.font = "64px Times New Roman"
        if(currGear == -1) {
            context.fillText("R", 0, 0, 200)
        } else {
            context.fillText(currGear, 0, 0, 200)
        }
        context.restore()

        context.save()
        context.translate(0, this.HEIGHT - 10)
        context.fillStyle = "rgb(255, 0, 0)"
        context.font = "32px Times New Roman"
        context.fillText(maxGear, 0, 0, 200)
        context.restore()
    }
}

modules.push(new Gear())