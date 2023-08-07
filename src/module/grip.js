class Grip {
    WIDTH = 300
    HEIGHT = 300
    X_SCALE = 1.0;
    Y_SCALE = 1.0;

    draw(context, data) {
        let slip = data["Wheel Slip"]

        context.save();
        context.strokeStyle = "rgb(0, 0, 0)"
        context.translate(0, 0);
        context.lineWidth = 1;
        context.strokeRect(0, 0, this.WIDTH, this.HEIGHT);
        context.restore()

        if(Math.abs((slip[0] + slip[1]) - (slip[2] + slip[3])) < 7) {
            context.save()
            context.fillStyle = "rgb(0, 255, 0)"
            context.translate(0, 100)
            context.font = "100px Times New Roman"
            context.fillText("Good", 0, 0, 300)
            context.restore()
        } else if((slip[0] + slip[1]) > (slip[2] + slip[3])) {
            context.save()
            context.fillStyle = "rgb(255, 0, 0)"
            context.translate(0, 200)
            context.font = "100px Times New Roman"
            context.fillText("Front Slip", 0, 0, 300)
            context.restore()
        } else {
            context.save()
            context.fillStyle = "rgb(0, 0, 255)"
            context.translate(0, 300)
            context.font = "100px Times New Roman"
            context.fillText("Rear Slip", 0, 0, 300)
            context.restore()
        }
    }
}

modules.push(new Grip)