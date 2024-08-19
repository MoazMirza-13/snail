import {PermissionsAndroid} from 'react-native';

export const callPermissions = async () => {
  try {
    const permissions = await PermissionsAndroid.requestMultiple([
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
      PermissionsAndroid.PERMISSIONS.READ_CALL_LOG,
    ]);
    console.log('Permissions are:', permissions);
  } catch (err) {
    console.warn(err);
  }
};
