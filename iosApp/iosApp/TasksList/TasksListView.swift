//
//  TasksListView.swift
//  iosApp
//
//  Created by Олег Иващенко on 19.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct TasksListView: View {
    @Environment(\.isPreview) var isPreview
    
    @StateObject
    private var viewModel: TasksListViewModelObservableObject
    
    @EnvironmentObject
    private var navigator: MainNavigatorObservableObject
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading) {
                ForEach(viewModel.state.tasks) { task in
                    HStack {
                        Text(task.title)
                            .padding()
                        Spacer()
                        Button (
                            action: {
                                viewModel.wrapped.onTaskEditClicked(task: task)
                            },
                            label: {
                                Image(systemName: "pencil")
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .foregroundColor(.gray)
                                    .frame(width: 32, height: 32)
                                    .padding()
                                //                                .background(Color.accentColor)
                                    .cornerRadius(12)
                                
                            }
                        )
                    }
                }
            }
            .cornerRadius(CGFloat(12))
            .padding()
        }
    }
    
    static func factory(isPreview: Bool = false) -> TasksListView {
        return TasksListView(
            viewModel: (isPreview ? FakeTasksListViewModel(count: 5) : TasksListComponent().tasksListViewModel()).asObservableObject()
        )
    }
}

extension Task: Identifiable {
    public var id: String { title }
}

struct TasksListView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListView.factory(isPreview: true)
                .environmentObject(FakeMainNavigator().asObservableObject())
        }
        .background(Color.gray)
        .cornerRadius(CGFloat(12))
    }
}
