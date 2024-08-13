import SoundPlayer from 'react-native-sound-player';

export const useCallState = () => {
  const playSound = async () => {
    try {
      SoundPlayer.playAsset(
        require('../android/app/src/main/res/raw/gacha.mp3'),
      );
    } catch (error) {
      console.log('cannot play the song file', error);
    }
  };

  return {
    playSound,
  };
};
