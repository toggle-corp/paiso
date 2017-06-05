import React from 'react';
import {
    ListView,
} from 'react-native';
import DashboardTransaction from './DashboardTransaction';


const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
export default function DashboardTransactionList(props) {
    const dataSource = ds.cloneWithRows(props.transactions);
    return (
        <ListView
            dataSource={dataSource}
            enableEmptySections={true}
            renderRow={(data) => <DashboardTransaction transaction={data} onSelect={() => props.onSelect(data.id)}/>}
        />
    );
}
