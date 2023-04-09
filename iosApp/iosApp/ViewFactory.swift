//
//  ViewFactory.swift
//  iosApp
//
//  Created by Олег Иващенко on 16.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

class ViewFactory {
    
    @ViewBuilder
    func makeView(_ destination: NavigationDestination) -> some View {
        switch NavigationDestinationKs(destination) {
        case .addTask:
            AddTaskView.factory()
        case .editTask(let value):
            EditTaskView.factory(uuid: value.uuid)
        }
    }
}
