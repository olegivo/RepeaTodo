import SwiftUI

struct SideMenuView: View {
    @Binding var presentSideMenu: Bool
    
    var body: some View {
        HStack {
            ZStack{
                VStack(alignment: .leading, spacing: 0) {
                    DrawerToDoListsView.factory()
                    Spacer()
                }
                .padding(.top, 100)
                .frame(width: 270)
                .background(
                    Color.white
                )
            }
            Spacer()
        }
        .background(.clear)
    }}

struct SideMenuView_Previews: PreviewProvider {
    static var previews: some View {
        SideMenuView(
            presentSideMenu: Binding.constant(true)
        )
    }
}
