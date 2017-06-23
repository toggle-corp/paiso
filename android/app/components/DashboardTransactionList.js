import React from 'react';
import {
    View,
    ListView,
} from 'react-native';
import DashboardTransaction from './DashboardTransaction';
import styles from '../styles/dashboard';


const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
export default function DashboardTransactionList(props) {
    const dataSource = ds.cloneWithRows(props.transactions);
    return (
        <ListView
            dataSource={dataSource}
            enableEmptySections={true}
            renderSeparator={() => <View style={styles.separator}/>}
            renderRow={(data) => <DashboardTransaction transaction={data} onSelect={() => props.onSelect(data.id)}/>}
        />
    );
}
