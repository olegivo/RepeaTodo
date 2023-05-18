//
//  MainView.swift
//  iosApp
//
//  Created by Олег Иващенко on 16.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MainView: View {
    @Environment(\.isPreview) var isPreview
    @Environment(\.navigator) var navigator: MainNavigatorObservableObject

    @StateObject
    private var viewModel: MainViewModelObservableObject

    var body: some View {
        NavigationView {
            VStack() {
                Spacer()
                Divider()
                TasksListView.factory(isPreview: isPreview)
                    .environmentObject(navigator)
                Divider()
                AddTaskInlinedView.factory(isPreview: isPreview)
                    .padding()
            }
        }
        .navigationTitle("Todos")
        .handleNavigation(navigator)
    }
    
    static func factory(isPreview: Bool = false) -> some View {
        let di = MainComponent()
        let navigator = (isPreview ? FakeMainNavigator() : di.mainNavigator()).asObservableObject()
        return MainView(
            viewModel: (isPreview ? FakeMainViewModel() : di.mainViewModel()).asObservableObject()
        ).navigator(navigator)
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView.factory(isPreview: true)
    }
}
