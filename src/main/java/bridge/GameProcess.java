package bridge;

import bridge.domain.Bridge;
import bridge.game.BridgeGame;
import bridge.domain.User;
import bridge.view.InputView;
import bridge.view.OutputView;


public class GameProcess {
    private BridgeGame bridgeGame;
    private BridgeMaker bridgeMaker;
    private InputView inputView;
    private OutputView outputView;
    private Boolean clear;

    public GameProcess(BridgeGame bridgeGame, BridgeMaker bridgeMaker, InputView inputView, OutputView outputView) {
        this.bridgeGame = bridgeGame;
        this.bridgeMaker = bridgeMaker;
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void start() {
        clear = false;
        User user = new User();
        outputView.println("다리 건너기 게임을 시작합니다.");

        boolean gameStart = true;

        repeat(gameStart, user);

        outputView.printResult(user.getPosition(), user.getRetry(), clear);
    }

    private void repeat(Boolean gameStart,User user) {
        while (gameStart) {
            //다리 길이 입력, 다리 생성
            Integer bridgeSize = inputView.readBridgeSize(e-> outputView.printError(e.getMessage()));
            Bridge bridge = new Bridge(bridgeMaker.makeBridge(bridgeSize));

            // 사용자 최종 위치 계산
            int userPosition = getUserLastPosition(bridgeSize, bridge, user);

            // 실패시 재시작 여부
            gameStart = retryOrNot(bridgeSize, userPosition, user);
        }
    }

    private boolean retryOrNot(Integer bridgeSize, int userPosition, User user) {
        return cleared(bridgeSize, userPosition)
                && bridgeGame.retry(inputView.readGameCommand(
                        e-> outputView.printError(e.getMessage())), user);
    }

    private boolean cleared(Integer bridgeSize, Integer userPosition) {
        clear = true;
        return userPosition != bridgeSize;
    }

    private int getUserLastPosition(Integer bridgeSize, Bridge bridge, User user) {
        int index = 0;
        String way;

        do {
            // 움직일 방향 입력
            way = inputView.readMoving((e)-> outputView.printError(e.getMessage()));
        } while (movable(index++, way, bridge, user) && index < bridgeSize );

        return index;
    }


    private boolean movable(int index, String way, Bridge bridge, User user) {
        // 이동 가능한지 확인
        boolean moved = bridgeGame.move(index, way, bridge, user);
        // 이동 결과 출력
        outputView.printMap(user.getPosition());

        return moved;
    }
}
