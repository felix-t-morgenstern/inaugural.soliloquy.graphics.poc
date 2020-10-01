package render;

import io.Timer;

public class Animation {
    private Texture[] _frames;
    private int _pointer;
    private double _framesPerSecond;

    private double _elapsedTime;
    private double _currentTime;
    private double _lastTime;

    public Animation(int amount, int framesPerSecond, String filename) {
        _pointer = 0;
        _elapsedTime = 0;
        _currentTime = 0;
        _lastTime = Timer.getTime();

        _framesPerSecond = 1.0/(double)framesPerSecond;

        _frames = new Texture[amount];

        for (int i = 0; i < amount; i++) {
            _frames[i] = new Texture("anim/" + filename + "_" + i + ".png");
        }
    }

    public void bind() {
        bind(0);
    }

    public void bind(int sampler) {
        _currentTime = Timer.getTime();

        _elapsedTime += _currentTime - _lastTime;

        if (_elapsedTime >= _framesPerSecond) {
            _elapsedTime -= _framesPerSecond;
            _pointer++;
        }

        if (_pointer >= _frames.length) {
            _pointer = 0;
        }

        _lastTime = _currentTime;

        _frames[_pointer].bind(sampler);
    }
}
