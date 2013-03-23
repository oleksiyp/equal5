package gui.mainapp.viewmodel;

import engine.calculation.ViewportBounds;

/**
* User: Oleksiy Pylypenko
* Date: 3/23/13
* Time: 1:32 PM
*/
class ActionHandler implements ActionVisitor {
    private EqualViewModel viewModel;

    public ActionHandler(EqualViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void refresh() {
        viewModel.notifyViewListeners(InterfacePart.VIEWPORT);
    }

    @Override
    public void play() {
    }

    @Override
    public void left() {
        ViewportBounds bounds = viewModel.getViewportBounds();
        double dx = bounds.getWidth() / EqualViewModel.MOVE_PART;
        viewModel.setViewportBounds(bounds.offset(-dx, 0));
    }

    @Override
    public void right() {
        ViewportBounds bounds = viewModel.getViewportBounds();
        double dx = bounds.getWidth() / EqualViewModel.MOVE_PART;
        viewModel.setViewportBounds(bounds.offset(dx, 0));
    }

    @Override
    public void up() {
        ViewportBounds boundsVal = viewModel.getViewportBounds();
        double dy = boundsVal.getHeight() / EqualViewModel.MOVE_PART;
        viewModel.setViewportBounds(boundsVal.offset(0, dy));
    }

    @Override
    public void down() {
        ViewportBounds bounds = viewModel.getViewportBounds();
        double dy = bounds.getHeight() / EqualViewModel.MOVE_PART;
        viewModel.setViewportBounds(bounds.offset(0, -dy));
    }

    @Override
    public void zoomIn() {
        ViewportBounds bounds = viewModel.getViewportBounds();
        viewModel.setViewportBounds(bounds.zoom(1.0 / EqualViewModel.ZOOM_COEFFICIENT));
    }

    @Override
    public void zoomOut() {
        ViewportBounds bounds = viewModel.getViewportBounds();
        viewModel.setViewportBounds(bounds.zoom(EqualViewModel.ZOOM_COEFFICIENT));
    }

    @Override
    public void lowerT() {
        int tVal = viewModel.getT();
        tVal--;
        if (tVal < 0) {
            tVal = 0;
        }
        viewModel.setT(tVal);
    }

    @Override
    public void raiseT() {
        int tVal = viewModel.getT();
        tVal++;
        int steps = viewModel.getSteps();
        if (tVal > steps) {
            tVal = steps;
        }
        viewModel.setT(tVal);
    }
}
