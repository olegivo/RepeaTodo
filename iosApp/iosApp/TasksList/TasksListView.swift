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
    
    @ObservedObject private var viewModel: TasksListViewModel
    
    @EnvironmentObject
    private var navigator: MainNavigatorObservableObject
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading) {
                ForEach(viewModel.tasks) { task in
                    HStack {
                        Text(task.title)
                            .padding()
                        Spacer()
                        Button (
                            action: {
                                viewModel.onTaskEditClicked(task: task)
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
    
    static func factory(_ previewEnvironment: PreviewEnvironment? = nil) -> TasksListView {
        let viewModel: TasksListViewModel = previewEnvironment?.get() ?? TasksListComponent().tasksListViewModel()
        return TasksListView(viewModel: viewModel)
    }
}

extension Task: Identifiable {
    public var id: String { title }
}

extension TasksListViewModel {
    var tasks: [Task] {
        get {
            return self.state(
                \.state,
                 equals: { $0 === $1 },
                 mapper: { $0.tasks }
            )
        }
    }
}

struct TasksListView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListView.factory(preview{ $0.taskListFakes() })
                .environmentObject(FakeMainNavigator().asObservableObject())
        }
        .background(Color.gray)
        .cornerRadius(CGFloat(12))
    }
}
