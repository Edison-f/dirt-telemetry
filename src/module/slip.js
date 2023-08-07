class WheelSlip {
    WIDTH = 120;
    HEIGHT = 250;
    X_SCALE = 1.0;
    Y_SCALE = 1.0;

    draw(context, data) {
        let slip = data["Wheel Slip"]

        for (let slipKey in slip) {
            slipKey *= 500;
        }

        context.save();
        context.strokeStyle = "rgb(0, 0, 0)"
        context.translate(0, 0);
        context.lineWidth = 1;
        context.strokeRect(0, 0, this.WIDTH, this.HEIGHT);
        context.restore()

        this.rect(context, data, 0, this.HEIGHT - slip[0] * 2, slip[0] * 2)
        this.rect(context, data, 70, this.HEIGHT - slip[1] * 2, slip[1] * 2)
        this.rect(context, data, 0, 120 - slip[2] * 2, slip[2] * 2)
        this.rect(context, data, 70, 120 - slip[3] * 2, slip[3] * 2)
    }

    rect(context, data, x, y, n) {
        context.save()
        context.fillStyle = "rgb(255, 0, 0)"
        context.translate(x, y);
        context.fillRect(0, 0, 50, n);
        context.restore();
    }
}

modules.push(new WheelSlip())